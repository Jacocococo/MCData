package com.jacoco.mcdata.files;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jacoco.mcdata.DarkMode;
import com.jacoco.mcdata.Gui;
import com.jacoco.mcdata.Strings;

public class Config {

	public static File jarDir;
	
	public static Path sourcesPath;
	public static Path sources;
	
	public static File cfg;
	
	public static String export;
	
	public Config() throws Exception {

		jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		
		sourcesPath = Paths.get(jarDir+"\\Sources");
		
		cfg = new File(sourcesPath+"\\"+Strings.config);
		boolean exists = cfg.exists(); 
		if(exists == false) {
			sources = Files.createDirectories(sourcesPath);
			ExportPath.exportDirPath = Paths.get(sourcesPath+"\\Export");
			ExportPath.exportDir = Files.createDirectories(ExportPath.exportDirPath);
			setupConfig();
		} else {

			Object obj = new JSONParser().parse(new FileReader(cfg));
			
			JSONObject jo = (JSONObject) obj; 
	        		
			String mode = jo.get("mode").toString();
						
			switch(mode) {
			
				// set color ints and label Strings when Config.json has "Light Mode"
				case Strings.light : DarkMode.setValues(254, 254, 254, 0, 0, 0, Strings.light);
				break;
					
				// set color ints and label Strings when Config.json has "Dark Mode"
				case Strings.dark : DarkMode.setValues(0, 0, 50, 254, 254, 254, Strings.dark);
				break; 
			}
			
			export = jo.get("Export Path").toString();
			ExportPath.exportDirPath = Paths.get(export);
			Gui.ep.setText(export);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setupConfig() throws Exception {
		// create Config.json
		
        JSONObject jo = new JSONObject(); 
        
        jo.put("mode", Strings.light);
        
        jo.put("Export Path", ExportPath.exportDirPath.toString());
        	          
        PrintWriter pw = new PrintWriter(sourcesPath+"\\"+Strings.config); 
        pw.write(jo.toJSONString()); 
          
        pw.flush(); 
        pw.close(); 
	}
	
}
