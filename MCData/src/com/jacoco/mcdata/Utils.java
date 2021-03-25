package com.jacoco.mcdata;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.jd.gui.service.platform.PlatformService;

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
}
