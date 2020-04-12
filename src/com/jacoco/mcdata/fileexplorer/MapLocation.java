package com.jacoco.mcdata.fileexplorer;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException; 

public class MapLocation {
	
	private String dotjson = ".json";
	private static String down = "downloads";
	private static String map = "client_mappings";
	
	public MapLocation(String s) {
		int feLength = s.length();
		int lastBackslash = s.lastIndexOf('\\');
		String fn = s.substring(lastBackslash, feLength);
		String loc = s; 
		String json = fn+dotjson;

        Object obj = null;
		try {
			obj = new JSONParser().parse(new FileReader(loc+json));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        JSONObject jo = (JSONObject) obj; 
        		
		String client_mappings = ((HashMap) jo.get(down)).get(map).toString();
		
		int client_mappingsLength = client_mappings.length();
		String mapurl = client_mappings.substring(73, client_mappingsLength-2).replaceAll("\\\\", "");
		System.out.println(mapurl);
	}
    	
}