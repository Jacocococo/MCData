package com.jacoco.mcdata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jd.gui.service.platform.PlatformService;

import com.jacoco.mcdata.files.Config;

public class Main {

	public static Path tmpDir;
	
	public static void main(String[] args) throws IOException {
		tmpDir = Files.createTempDirectory("MCData_");
		tmpDir.toFile().deleteOnExit();
		
		Config cfg = new Config();
		
		{ // Get NFD4j lib
			Path sourcesPath = cfg.getSourcesPath();
			String baseUrl = "https://github.com/Jacocococo/NFD4j/releases/latest/download/";
			switch (PlatformService.getInstance().getOS()) {
				case Windows:
					String libNameWin = "nfd4j.dll";
					Path libPathWin = sourcesPath.resolve(libNameWin);
					if (!libPathWin.toFile().exists()) {
						downloadFile(libPathWin, baseUrl + libNameWin);
					}
					System.setProperty("nfd.libPath", libPathWin.toString());
					break;
				case MacOSX:
					String libNameMac = "libnfd4j.dylib";
					Path libPathMac = sourcesPath.resolve(libNameMac);
					if (!libPathMac.toFile().exists()) {
						downloadFile(libPathMac, baseUrl + libNameMac);
					}	
					System.setProperty("nfd.libPath", libPathMac.toString());
					break;
				case Linux:
					String libName = "libnfd4j.so";
					Path libPath = sourcesPath.resolve(libName);
					if (!libPath.toFile().exists()) {
						downloadFile(libPath, baseUrl + libName);
					}
					System.setProperty("nfd.libPath", libPath.toString());
					break;
				default:
					break;
			}
		}
		
		try {
			if (PlatformService.getInstance().isLinux()) {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		Gui gui = new Gui(cfg);
		gui.show();
	}
	
	private static void downloadFile(Path path, String url) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(new URL(url).openStream());
		FileOutputStream fos = new FileOutputStream(path.toFile());
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}
}