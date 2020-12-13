package com.jacoco.mcdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.jacoco.mcdata.files.Config;

public class Main {

	private static Path tmpDir;
	private static Config cfg;
	private static Gui gui;
	
	public static void main(String[] args) throws IOException {
		tmpDir = Files.createTempDirectory("MCData_");
		tmpDir.toFile().deleteOnExit();
		cfg = new Config();
		gui = new Gui(cfg.getTheme(), cfg);
	}

	public static Path getTmpDir() {
		return tmpDir;
	}

	public static Config getCfg() {
		return cfg;
	}

	public static Gui getGui() {
		return gui;
	}
}
