package com.jacoco.mcdata.version;

import java.util.ArrayList;
import java.util.List;

public class Versions {

	private Versions() {}
	
	private static List<Version> versions = new ArrayList<Version>();
	private static int current = 0;
	
	public static List<Version> getVersions() {
		return versions;
	}
	
	public static Version getCurrent() {
		try {
			return versions.get(current);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public static void add(Version version) {
		int index = findVersionFromName(version.getName());
		if(index != -1) {
			current = index;
			return;
		}
		
		versions.add(version);
		current = versions.size()-1;
	}
	
	public static void remove(String name) {
		versions.remove(findVersionFromName(name));
	}
	
	public static void removeIfUnused(String name) {
		int index = findVersionFromName(name);
		if(versions.get(index).getExportedJar() == null)
			versions.remove(index);
	}
	
	public static void remove(Version version) {
		if(version != null)
			remove(version.getName());
	}
	
	public static boolean removeIfUnused(Version version) {
		if(version != null) {
			removeIfUnused(version.getName());
			return true;
		}
		return false;
	}
	
	public static void clear() {
		versions.clear();
	}
	
	private static int findVersionFromName(String name) {
		for(int i = 0; i < versions.size(); i++)
			if(versions.get(i).getName().equals(name))
				return i;
		
		return -1;
	}
}
