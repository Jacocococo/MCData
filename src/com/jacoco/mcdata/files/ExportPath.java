package com.jacoco.mcdata.files;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExportPath {

	public static Path exportDirPath;
	public static Path exportDir;
	
	@SuppressWarnings("unchecked")
	public static void setExportPath(String path) {
		
		// update Config.json
		FileReader reader = null;
		try {
			reader = new FileReader(Config.cfg);
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		}
	    JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(reader);
		} catch (IOException | ParseException e2) {
			e2.printStackTrace();
		}
		
	    jsonObject.put("Export Path", path);
	    
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(Config.cfg);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
        pw.write(jsonObject.toJSONString()); 
        
        pw.flush(); 
        pw.close();
	}
}
