package com.jacoco.mcdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jacoco.mcdata.files.Config;

public class Main {

	public static Path tmpDir;
	
	public static void main(String[] args) throws IOException {
		tmpDir = Files.createTempDirectory("MCData_");
		tmpDir.toFile().deleteOnExit();
		
		Config cfg = new Config();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		Gui gui = new Gui(cfg);
		gui.show();
	}
}