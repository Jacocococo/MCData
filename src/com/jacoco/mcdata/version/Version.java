package com.jacoco.mcdata.version;

import java.nio.file.Path;

import com.jacoco.mcdata.files.ObfuscationMap;

public class Version {

	private String name;
	private Path oJar;
	private Path eJar;
	private Path json;
	private ObfuscationMap obfuscationMap;
	
	public Version(Path path) {
		this.name = path.getFileName().toString();
		this.oJar = path.resolve(name + ".jar");
		this.json = path.resolve(name + ".json");
		this.obfuscationMap = new ObfuscationMap(this);
	}

	public String getName() {
		return name;
	}
	
	public Path getOriginalJar() {
		return oJar;
	}

	public Path getExportedJar() {
		return eJar;
	}

	public Path getJson() {
		return json;
	}

	public ObfuscationMap getObfuscationMap() {
		return obfuscationMap;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOriginalJar(Path oJar) {
		this.oJar = oJar;
	}
	
	public void setExportedJar(Path eJar) {
		this.eJar = eJar;
	}

	public void setJson(Path json) {
		this.json = json;
	}

	public void setObfuscationMap(ObfuscationMap obfuscationMap) {
		this.obfuscationMap = obfuscationMap;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
