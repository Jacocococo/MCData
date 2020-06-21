package com.jacoco.mcdata.files;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jacoco.mcdata.Strings; 

public class MapLocation {

	public static Path tmpFileMap;
	public static Path jar;
	public static String fn;
		
	// get jar file and map
	public MapLocation(String loc, Path tmpDir) throws IOException {
		
		// values needed
		int feLength = loc.length();
		int lastBackslash = loc.lastIndexOf('\\');
		fn = loc.substring(lastBackslash, feLength);
		jar = Paths.get(loc+fn+Strings.dotjar);
		String json = fn+Strings.dotjson;
				
		// finding the map
       	Object obj = null;
    	try {
   			obj = new JSONParser().parse(new FileReader(loc+json));
   		} catch (IOException | ParseException e) {
   			e.printStackTrace();
   		}
    	JSONObject jo = (JSONObject) obj; 
        
    	@SuppressWarnings("rawtypes")
   		String client_mappings = ((HashMap) jo.get("downloads")).get("client_mappings").toString();
   		
   		URL mapurl = new URL(client_mappings.substring(73, client_mappings.length()-2).replaceAll("\\\\", ""));
   		
   		// creating the 
   		tmpFileMap = Files.createTempFile(tmpDir,  fn.substring(1, fn.length())+"_", Strings.dottxt);
   		try (DirectoryStream<Path> ds = Files.newDirectoryStream(tmpDir)) {
   		  tmpDir.toFile().deleteOnExit();
   		  for (Path file: ds) {      
   		    file.toFile().deleteOnExit();
   		  }
   		}
   		
   		// print the map to the tempfile
        ReadableByteChannel rbc = Channels.newChannel(mapurl.openStream());
        FileOutputStream fos = new FileOutputStream(tmpFileMap.toString());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
           		   		
	}    	
}