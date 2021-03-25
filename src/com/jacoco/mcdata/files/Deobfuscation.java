package com.jacoco.mcdata.files;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.jacoco.mcdata.Main;
import com.jacoco.mcdata.version.Version;

import cuchaz.enigma.Enigma;
import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.gui.dialog.ProgressDialog.ProgressRunnable;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import cuchaz.enigma.utils.Utils;

public class Deobfuscation {

	private Enigma enigma;
	private MappingFormat format;
	private Runnable onFinish;
	
	public Deobfuscation(MappingFormat format, EnigmaProfile profile) {
		this.enigma = Enigma.builder().setProfile(profile).build();
		this.format = format;
	}
	
	public void export(Path exportPath, Version version) {
		version.setExportedJar(exportPath.resolve(version.getName() + ".jar"));

		try (ProgressDialog pd = new ProgressDialog()) {
			pd.runOffThread(progress -> {
				pd.frame.setTitle("Exporting Jar 1/4");
				EnigmaProject project = enigma.openJar(version.getOriginalJar(), progress);
				
				pd.frame.setTitle("Exporting Jar 2/4");
				Path downloadedMap = version.getObfuscationMap().downloadFile(Main.tmpDir, progress);
				
				pd.frame.setTitle("Exporting Jar 3/4");
				EntryTree<EntryMapping> mappings = format.read(downloadedMap, progress, null);
				project.setMappings(mappings);
	
				EnigmaProject.JarExport jar = project.exportRemappedJar(progress);
				pd.frame.setTitle("Exporting Jar 4/4");
				jar.write(version.getExportedJar(), progress);
			});
		}
	}
	
	public void addOnFinishEvent(Runnable runnable) {
		this.onFinish = runnable;
	}

	class ProgressDialog implements ProgressListener, AutoCloseable {
		private JFrame frame = new JFrame(String.format("Progress", new Object[] { "Enigma" }));
		
		private JLabel labelTitle;

		private JLabel labelText;

		private JProgressBar progress;
		
		public void setup() {
			Container pane = this.frame.getContentPane();
			FlowLayout layout = new FlowLayout();
			layout.setAlignment(0);
			pane.setLayout(layout);
			this.labelTitle = new JLabel();
			pane.add(this.labelTitle);
			JPanel panel = new JPanel();
			pane.add(panel);
			panel.setLayout(new BorderLayout());
			this.labelText = Utils.unboldLabel(new JLabel());
			this.progress = new JProgressBar();
			this.labelText.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
			panel.add(this.labelText, "North");
			panel.add(this.progress, "Center");
			panel.setPreferredSize(new Dimension(360, 50));
			pane.doLayout();
			this.frame.setSize(400, 120);
			this.frame.setResizable(false);
			this.frame.setLocationRelativeTo(null);
			this.frame.setVisible(true);
			this.frame.setDefaultCloseOperation(0);
		}

		public CompletableFuture<Void> runOffThread(ProgressRunnable runnable) {
			OnExport onExport = new OnExport() {
				public Void run() {
					try (ProgressDialog progress = ProgressDialog.this) {
						progress.setup();
						runnable.run(progress);
					} catch (Exception ex) {
						throw new Error(ex);
					}
					return null;
				}

				public Void finish(Void arg) {
					onFinish.run();
					return null;
				}
			};
			
			CompletableFuture<Void> future = CompletableFuture.supplyAsync(onExport::run);
			future.thenApplyAsync(onExport::finish);
			
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
	}
}
