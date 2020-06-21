package com.jacoco.mcdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.jacoco.mcdata.files.ExportPath;
import com.jacoco.mcdata.files.MapLocation;

import cuchaz.enigma.Enigma;
import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.gui.Gui;
import cuchaz.enigma.throwables.MappingParseException;
import cuchaz.enigma.translation.mapping.EntryMapping;
import cuchaz.enigma.translation.mapping.MappingSaveParameters;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.tree.EntryTree;
import cuchaz.enigma.utils.Utils;

public class Deobfuscation {

	public static Gui gui;

	public static Enigma enigma = null;
	private static EnigmaProfile profile = null;	

	public static EnigmaProject project;
	
	private static Path export;

	public static void export() throws IOException, MappingParseException {
		
		export = Paths.get(ExportPath.exportDirPath+"\\"+MapLocation.fn.substring(1, MapLocation.fn.length())+Strings.dotjar);

		profile = EnigmaProfile.EMPTY;

		enigma = Enigma.builder().setProfile(profile).build();

		MappingFormat format = MappingFormat.PROGUARD;

		MappingSaveParameters saveParameters = enigma.getProfile().getMappingSaveParameters();

		JFrame f = new JFrame();
		
		ProgressDialog.runOffThread(f, progress -> {
			project = enigma.openJar(MapLocation.jar, progress);
			
			EntryTree<EntryMapping> mappings = format.read(MapLocation.tmpFileMap, progress, saveParameters);
			project.setMappings(mappings);
						
			EnigmaProject.JarExport jar = project.exportRemappedJar(progress);
	        jar.write(export, progress);
		});
		
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
    this.labelText = Utils.unboldLabel(new JLabel());
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
