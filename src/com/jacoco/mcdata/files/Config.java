package com.jacoco.mcdata.files;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.jacoco.mcdata.Utils;

public class Config {

	private JSONObject jo;
	
	private Path sourcesPath;
	private File cfg;
	
	private Path export;
	
	@SuppressWarnings("unchecked")
	public Config() {		
		try {
			this.sourcesPath = new File("Sources").getAbsoluteFile().toPath();
			this.cfg = this.sourcesPath.resolve("Config.json").toFile();

			if(!cfg.exists()) {
				Files.createDirectories(this.sourcesPath);
				this.export = this.sourcesPath.resolve("Export");
				Files.createDirectories(this.export);
				
				this.jo = new JSONObject(); 
		        jo.put("exportPath", this.export.toString());
		        jo.put("jd", null);
		        
		        try (PrintWriter writer = new PrintWriter(cfg)) {
		        	writer.write(Utils.prettyPrint(jo.toJSONString())); 
		        }
			} else {
				this.jo = (JSONObject) JSONValue.parse(new FileReader(this.cfg)); 

				try{
					export = Paths.get(jo.get("exportPath").toString());
				} catch (NullPointerException e) {
					export = this.sourcesPath.resolve("Export");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Path getSourcesPath() {
		return this.sourcesPath;
	}
	
	public Path getExportPath() {
		return this.export;
	}
	
	public JSONObject getJD() {
		return (JSONObject) this.jo.get("jd");
	}
	
	@SuppressWarnings("unchecked")
	public void setExportPath(String path) {
		this.export = Paths.get(path);
		this.jo.replace("exportPath", path);
		write();
	}
	
	@SuppressWarnings("unchecked")
	public void saveJDConfig(JSONObject jo) {
		this.jo.replace("jd", jo);
		write();
	}
	
	private void write() {
		try (PrintWriter writer = new PrintWriter(this.cfg)) {
			writer.write(Utils.prettyPrint(this.jo.toJSONString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
