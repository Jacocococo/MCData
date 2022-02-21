package com.jacoco.mcdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

import com.google.common.base.Functions;
import com.jacoco.mcdata.files.ExportPath;
import com.jacoco.mcdata.files.MapLocation;

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
import cuchaz.enigma.gui.Gui;
import cuchaz.enigma.translation.ProposingTranslator;
import cuchaz.enigma.translation.Translator;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.EntryRemapper;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import cuchaz.enigma.translation.representation.entry.ClassDefEntry;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.utils.I18n;
import cuchaz.enigma.utils.Utils;

public class Deobfuscation {

	public static Gui gui;

	public static Enigma enigma = null;
	private static EnigmaProfile profile = null;	

	public static EnigmaProject project;
	
	private static Path export;

	public static void export() throws IOException {
		
		export = ExportPath.exportDirPath.resolve(MapLocation.fn+Strings.dotjar);

		profile = EnigmaProfile.EMPTY;

		enigma = Enigma.builder().setProfile(profile).build();

		MappingFormat format = MappingFormat.PROGUARD;

		JFrame f = new JFrame();
		
		ProgressDialog.runOffThread(f, progress -> {
			project = openJar(MapLocation.jar, (ClassProvider) new ClasspathClassProvider(), progress);
			
			EntryTree<EntryMapping> mappings = format.read(MapLocation.tmpFileMap, progress, null);
			project.setMappings(mappings);
						
			EnigmaProject.JarExport jar = project.exportRemappedJar(progress);
	        jar.write(export, progress);
		});
		
	}
	
	private static EnigmaProject openJar(Path path, ClassProvider libraryClassProvider, ProgressListener progress) throws IOException {
		JarClassProvider jarClassProvider = new JarClassProvider(path);
		ClassProvider classProvider = new CachingClassProvider(new CombiningClassProvider(jarClassProvider, libraryClassProvider));
		Set<String> scope = jarClassProvider.getClassNames();

		JarIndex index = JarIndex.empty();
		index.indexJar(scope, classProvider, progress);
		enigma.getServices().get(JarIndexerService.TYPE).forEach(indexer -> indexer.acceptJar(scope, classProvider, index));

		return new EnigmaProject(enigma, path, classProvider, index, Utils.zipSha1(path)) {
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
}

class ProgressDialog implements ProgressListener, AutoCloseable {
  private JFrame frame = new JFrame(String.format("Exporting Jar", new Object[] { "Enigma" }));
  
  private JLabel labelTitle;
  
  private JLabel labelText;
  
  private JProgressBar progress;
  
  public ProgressDialog(JFrame parent) {
    Container pane = this.frame.getContentPane();
    FlowLayout layout = new FlowLayout();
    layout.setAlignment(0);
    pane.setLayout(layout);
    this.labelTitle = new JLabel();
    pane.add(this.labelTitle);
    JPanel panel = new JPanel();
    pane.add(panel);
    panel.setLayout(new BorderLayout());
    this.labelText = new JLabel();
    this.progress = new JProgressBar();
    this.labelText.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    panel.add(this.labelText, "North");
    panel.add(this.progress, "Center");
    panel.setPreferredSize(new Dimension(360, 50));
    pane.doLayout();
    this.frame.setSize(400, 120);
    this.frame.setResizable(false);
    this.frame.setLocationRelativeTo(parent);
    this.frame.setVisible(true);
    this.frame.setDefaultCloseOperation(0);
  }
  
  public static CompletableFuture<Void> runOffThread(JFrame parent, ProgressRunnable runnable) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    (new Thread(() -> {
          try (ProgressDialog progress = new ProgressDialog(parent)) {
            runnable.run(progress);
            future.complete(null);
          } catch (Exception ex) {
            future.completeExceptionally(ex);
            throw new Error(ex);
          } 
        })).start();
    return future;
  }
  
  public void close() {
    this.frame.dispose();
  }
  
  public void init(int totalWork, String title) {
    this.labelTitle.setText(title);
    this.progress.setMinimum(0);
    this.progress.setMaximum(totalWork);
    this.progress.setValue(0);
  }
  
  public void step(int numDone, String message) {
    this.labelText.setText(message);
    if (numDone != -1) {
      this.progress.setValue(numDone);
      this.progress.setIndeterminate(false);
    } else {
      this.progress.setIndeterminate(true);
    } 
    this.frame.validate();
    this.frame.repaint();
  }
  
  public static interface ProgressRunnable {
    void run(ProgressListener param1ProgressListener) throws Exception;
  }
}
