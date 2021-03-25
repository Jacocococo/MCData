package com.jacoco.mcdata.version;

import java.util.ArrayList;
import java.util.List;

public class Versions {

	private Versions() {}
	
	private static List<Version> versions = new ArrayList<Version>();
	
	public static List<Version> getVersions() {
		return versions;
	}
	
	public static Version getLatest() {
		return versions.get(versions.size()-1);
	}
	
	public static void add(Version version) {
		versions.add(version);
	}
	
	public static void remove(Version version) {
		versions.remove(version);
	}
	
	public static void clear() {
		versions.clear();
	}
}
