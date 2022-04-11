package com.jacoco.mcdata.files;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

import com.google.common.base.Functions;
import com.jacoco.mcdata.Main;
import com.jacoco.mcdata.Utils;
import com.jacoco.mcdata.version.Version;

import cuchaz.enigma.Enigma;
import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.analysis.index.JarIndex;
import cuchaz.enigma.api.service.JarIndexerService;
import cuchaz.enigma.api.service.NameProposalService;
import cuchaz.enigma.bytecode.translators.TranslationClassVisitor;
import cuchaz.enigma.classprovider.CachingClassProvider;
import cuchaz.enigma.classprovider.ClassProvider;
import cuchaz.enigma.classprovider.ClasspathClassProvider;
import cuchaz.enigma.classprovider.CombiningClassProvider;
import cuchaz.enigma.classprovider.JarClassProvider;
import cuchaz.enigma.classprovider.ObfuscationFixClassProvider;
import cuchaz.enigma.translation.ProposingTranslator;
import cuchaz.enigma.translation.Translator;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import cuchaz.enigma.translation.representation.entry.ClassDefEntry;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.utils.I18n;

public class Deobfuscation {

	private Enigma enigma;
	private MappingFormat format;
	private Runnable onFinish;
	
	public Deobfuscation(MappingFormat format, EnigmaProfile profile) {
		this.enigma = Enigma.builder().setProfile(profile).build();
		this.format = format;
	}
	
	public void export(Path exportPath, Version version) {
		Path exportedJar = exportPath.resolve(version.getName() + ".jar");
		version.setExportedJar(exportedJar);
		
		if(exportedJar.toFile().exists()) {
			onFinish.run();
			return;
		}

		Utils.mapProgressListener(progress -> {
			EnigmaProject project = openJar(version.getOriginalJar(), (ClassProvider) new ClasspathClassProvider(), progress);
			
			Path downloadedMap = version.getObfuscationMap().downloadFile(Main.tmpDir, progress);
			
			EntryTree<EntryMapping> mappings = format.read(downloadedMap, progress, this.enigma.getProfile().getMappingSaveParameters());
			project.setMappings(mappings);

			EnigmaProject.JarExport jar = project.exportRemappedJar(progress);
			jar.write(version.getExportedJar(), progress);
			
			onFinish.run();
		});
	}
	
	private EnigmaProject openJar(Path path, ClassProvider libraryClassProvider, ProgressListener progress) throws IOException {
		JarClassProvider jarClassProvider = new JarClassProvider(path);
		ClassProvider classProvider = new CachingClassProvider(new CombiningClassProvider(jarClassProvider, libraryClassProvider));
		Set<String> scope = jarClassProvider.getClassNames();

		JarIndex index = JarIndex.empty();
		index.indexJar(scope, classProvider, progress);
		enigma.getServices().get(JarIndexerService.TYPE).forEach(indexer -> indexer.acceptJar(scope, classProvider, index));

		return new EnigmaProject(enigma, path, classProvider, index, cuchaz.enigma.utils.Utils.zipSha1(path)) {
			public JarExport exportRemappedJar(ProgressListener progress) {
				Collection<ClassEntry> classEntries = getJarIndex().getEntryIndex().getClasses();
				ClassProvider fixingClassProvider = new ObfuscationFixClassProvider(getClassProvider(), getJarIndex());

				NameProposalService[] nameProposalServices = getEnigma().getServices().get(NameProposalService.TYPE).toArray(new NameProposalService[0]);
				Translator deobfuscator = nameProposalServices.length == 0 ? getMapper().getDeobfuscator() : new ProposingTranslator(getMapper(), nameProposalServices);

				AtomicInteger count = new AtomicInteger();
				progress.init(classEntries.size(), I18n.translate("progress.classes.deobfuscating"));

				Map<String, ClassNode> compiled = classEntries.parallelStream()
						.map(entry -> {
							@SuppressWarnings("deprecation")
							ClassEntry translatedEntry = deobfuscator.translate(entry);
							progress.step(count.getAndIncrement(), translatedEntry.toString());

							ClassNode node = fixingClassProvider.get(entry.getFullName());
							if (node != null) {
								ClassNode translatedNode = new ClassNode();
								if(((ClassDefEntry) entry).getSuperClass().getName().equals("java/lang/Record")) {
									for(int i = 0; i < node.methods.size(); i++) {
										for(AbstractInsnNode insn : node.methods.get(i).instructions) {
											if(insn instanceof InvokeDynamicInsnNode) {
												InvokeDynamicInsnNode newInsn = (InvokeDynamicInsnNode) insn;
												for(int j = 0; j < newInsn.bsmArgs.length; j++) {
													if(newInsn.bsmArgs[j] instanceof Handle) {
														try {
															Field fDesc = Handle.class.getDeclaredField("descriptor");
															fDesc.setAccessible(true);
															String desc = (String) fDesc.get(((Handle) newInsn.bsmArgs[j]));
															if(!desc.startsWith("("))
																fDesc.set(((Handle) newInsn.bsmArgs[j]), "()" + desc);
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												}
												node.methods.get(i).instructions.set(insn, newInsn);
											}
										}
									}
								}
								node.accept(new TranslationClassVisitor(deobfuscator, Enigma.ASM_VERSION, translatedNode));
								return translatedNode;
							}

							return null;
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toMap(n -> n.name, Functions.identity()));

				try {
					Constructor<JarExport> constructor = JarExport.class.getDeclaredConstructor(EntryRemapper.class, Map.class);
					constructor.setAccessible(true);
					return constructor.newInstance(getMapper(), compiled);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}
	
	public void addOnFinishEvent(Runnable runnable) {
		this.onFinish = runnable;
	}
}
