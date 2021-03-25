package com.jacoco.mcdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.jacoco.mcdata.files.Config;

public class Main {

	public static Path tmpDir;
	
	public static void main(String[] args) throws IOException {
		tmpDir = Files.createTempDirectory("MCData_");
		tmpDir.toFile().deleteOnExit();
		Config cfg = new Config();
		new Gui(cfg);
	}
}