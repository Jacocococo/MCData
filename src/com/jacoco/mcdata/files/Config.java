package com.jacoco.mcdata.files;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.jacoco.mcdata.Utils;
import com.jacoco.mcdata.Theme;

public class Config {

	private final String fn = "Config.json";
	
	private JSONObject jo;
	
	private Path sourcesPath;
	private File cfg;
	
	private Theme thm;
	private String theme;
	private Path export;
	
	public Config() {
		this.sourcesPath = Paths.get(ClassLoader.getSystemClassLoader().getResource(".").getPath().substring(1)).resolve("Sources");
		this.cfg = this.sourcesPath.resolve(fn).toFile();
		
		try {
			if(!cfg.exists()) {
				Files.createDirectories(this.sourcesPath);
				this.export = this.sourcesPath.resolve("Export");
				Files.createDirectories(this.export);
				setupConfig(this.cfg);
				this.thm = Theme.LIGHT;
			} else {
				this.jo = (JSONObject) JSONValue.parse(new FileReader(this.cfg)); 
				this.theme = jo.get("theme").toString();

				try{
					export = Paths.get(jo.get("exportPath").toString());
				} catch (NullPointerException e) {
					export = this.sourcesPath.resolve("Export");
				}
				
				switch(this.theme) {
					case "Light Mode" : this.thm = Theme.LIGHT;
					break;
					case "Dark Mode" : this.thm = Theme.DARK;
					break; 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setupConfig(File path) throws IOException {
        this.jo = new JSONObject(); 
        
        jo.put("theme", "Light Mode");
        jo.put("exportPath", this.export.toString());
        jo.put("jd", null);
        
        PrintWriter writer = new PrintWriter(cfg);
        writer.write(Utils.prettyPrint(jo.toJSONString())); 
        writer.close(); 
	}
	
	public Path getSourcesPath() {
		return this.sourcesPath;
	}
	
	public Path getExportPath() {
		return this.export;
	}
	
	public Theme getTheme() {
		return this.thm;
	}
	
	public JSONObject getJD() {
		return (JSONObject) this.jo.get("jd");
	}
	
	@SuppressWarnings("unchecked")
	public void setExportPath(String path) {
		try {
			this.export = Paths.get(path);
			this.jo.replace("exportPath", path);
			PrintWriter writer = new PrintWriter(this.cfg);
		    writer.write(Utils.prettyPrint(this.jo.toJSONString()));
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void setTheme(String color) {
		try {
			this.thm = Theme.getThemeFromName(color);
		    this.jo.replace("theme", color);
			PrintWriter writer = new PrintWriter(this.cfg);
		    writer.write(Utils.prettyPrint(this.jo.toJSONString()));
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveJDConfig(JSONObject jo) {
		try {
			this.jo.replace("jd", jo);
			PrintWriter writer;
			writer = new PrintWriter(this.cfg);
		    writer.write(Utils.prettyPrint(this.jo.toJSONString()));
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
