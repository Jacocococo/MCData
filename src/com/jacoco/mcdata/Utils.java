package com.jacoco.mcdata;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.jd.gui.service.platform.PlatformService;

import com.jacoco.mcdata.controller.ProgressController;

import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.gui.dialog.ProgressDialog.ProgressRunnable;

public class Utils {

	private static List<Character> jsonSpecials = Arrays.asList('"', '\\', '/', 'b', 'f', 'n', 'r', 't');
	
	public static String prettyPrint(String json) {
		StringReader reader = new StringReader(json);
		StringBuilder builder = new StringBuilder();
		int depth = 0;
		
		try {
			Character c = ' ';
			while((c = (char) reader.read()) < 255 && c > 0) {
				switch(c) {
					case '"':
						boolean isString = true;
						while(isString) {
							builder.append(c);
							c = (char) reader.read();
							if(c.equals('\\')) {
								builder.append(c);
								if(!jsonSpecials.contains(c = (char) reader.read()))
									builder.append('\\');
							} else if(c.equals('"')) {
								builder.append(c);
								isString = false;
							}
						}
						break;
					case '{':
					case '[':
						builder.append(c);
						builder.append("\n");
						for(int i = 0; i < depth+1; i++) builder.append("\t");
						depth++;
						break;
					case '}':
					case ']':
						Character lastChar = builder.charAt(builder.length()-1);
						if(lastChar.equals('\t')) {
							builder.deleteCharAt(builder.length()-1);
						} else {
							builder.append("\n");
							for(int i = 0; i < depth-1; i++) builder.append("\t");
						}
						builder.append(c);
						depth--;
						break;
					case ',':
						builder.append(c);
						builder.append("\n");
						for(int i = 0; i < depth; i++) builder.append("\t");
						break;
					case ':':
						builder.append(c); builder.append(' ');
						break;
					default:
						builder.append(c);
						break;
				}
			}
		} catch (IOException e) {
			return null;
		}
		
		return builder.toString();
	}
	
	public static File getMinecraftDir() {
		String workingDirectory;
		if (PlatformService.getInstance().isWindows()) {
		    workingDirectory = System.getenv("AppData");
		} else {
		    workingDirectory = System.getProperty("user.home");
		    if(PlatformService.getInstance().isMac())
		    	workingDirectory += "/Library/Application Support";
		}
		return Paths.get(workingDirectory).resolve(".minecraft").toFile();
	}
	
	public static void mapProgressListener(ProgressRunnable runnable) {
		CompletableFuture.runAsync(() -> {
			try {
				AtomicInteger workInCurrent = new AtomicInteger(0);
				ProgressController controller = ProgressController.getInstance();
				controller.newProgress(0);
				runnable.run(new ProgressListener() {
					public void init(int totalWork, String s) {
						controller.increaseMaximum(totalWork);
						workInCurrent.set(0);
					}
					
					public void step(int numDone, String s) {
						int value = numDone - workInCurrent.get();
						workInCurrent.set(numDone);
						controller.step(value);
					}
				});
				controller.finishProgress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
