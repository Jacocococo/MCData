package com.jacoco.mcdata.files;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.jacoco.mcdata.version.Versions;

import cuchaz.enigma.ProgressListener; 

public class ObfuscationMap {

	private Path fileMap;
	private URL mapurl;
		
	public ObfuscationMap(Path path) {
		try {
			JSONObject jo = (JSONObject) ((JSONObject)  ((JSONObject) 
					JSONValue.parse(new FileReader(path.toString()))).get("downloads")).get("client_mappings");
			this.mapurl = new URL(jo.get("url").toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Path downloadFile(Path tmpDir, ProgressListener progress) {
   		try {
   			progress.init(3, "Downloading Obfuscation Map as a Temp File");
			
   			progress.step(1, "Creating Temp File");
   			fileMap = Files.createTempFile(tmpDir, Versions.getLatest().getName() + "_", ".txt");
   			fileMap.toFile().deleteOnExit();
   			
	   		progress.step(2, "Connecting to URL");
	        ReadableByteChannel rbc = Channels.newChannel(mapurl.openStream());
	        progress.step(3, "Writing File");
	        FileOutputStream fos = new FileOutputStream(fileMap.toFile());
	        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        fos.close();
	        rbc.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
   		
   		return this.fileMap;
	}
	
	public Path getFile() throws FileNotFoundException {
		if(this.fileMap.toFile().exists())
			return this.fileMap;
		else
			throw new FileNotFoundException();
	}
	
	public URL getURL() {
		return this.mapurl;
	}
}