package com.jacoco.mcdata;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jacoco.mcdata.files.Config;

public class DarkMode {
	
	@SuppressWarnings("unchecked")
	public static void setMode(String color) {
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
		
	    jsonObject.put("mode", color);
	    
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
