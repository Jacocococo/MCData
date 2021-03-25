/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui.service;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import org.jd.gui.Constants;
import org.jd.gui.model.configuration.Configuration;
import org.jd.gui.util.exception.ExceptionUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jacoco.mcdata.files.Config;

public class ConfigurationPersister {
	protected static final String ERROR_BACKGROUND_COLOR = "JdGuiPreferences.errorBackgroundColor";
    protected static final String JD_CORE_VERSION = "JdGuiPreferences.jdCoreVersion";

    protected Config cfg;
    
    public ConfigurationPersister(Config cfg) {
		this.cfg = cfg;
	}

	public Configuration load() {
        // Default values
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int w = (screenSize.width>Constants.DEFAULT_WIDTH) ? Constants.DEFAULT_WIDTH : screenSize.width;
        int h = (screenSize.height>Constants.DEFAULT_HEIGHT) ? Constants.DEFAULT_HEIGHT : screenSize.height;
        int x = (screenSize.width-w)/2;
        int y = (screenSize.height-h)/2;
        
        Configuration config = new Configuration();
        config.setMainWindowLocation(new Point(x, y));
        config.setMainWindowSize(new Dimension(w, h));
        config.setMainWindowMaximize(false);

        File recentSaveDirectory = new File(System.getProperty("user.dir"));

        config.setRecentLoadDirectory(recentSaveDirectory);
        config.setRecentSaveDirectory(recentSaveDirectory);

        JSONObject jo = cfg.getJD();
		if(jo != null) {
			JSONObject gui = (JSONObject) jo.get("gui");
    			JSONObject mainWindow = (JSONObject) gui.get("mainWindow");
    				JSONObject location = (JSONObject) mainWindow.get("location");
    					Point loc = new Point(Integer.parseInt(location.get("x").toString()), Integer.parseInt(location.get("y").toString()));
    					config.setMainWindowLocation(loc);
    				JSONObject size = (JSONObject) mainWindow.get("size");
    					Dimension dim = new Dimension(Integer.parseInt(size.get("w").toString()), Integer.parseInt(size.get("h").toString()));
    					config.setMainWindowSize(dim);
    				config.setMainWindowMaximize(Boolean.valueOf(mainWindow.get("maximize").toString()));
    		JSONArray recentFilePaths = (JSONArray) jo.get("recentFilePaths");
        		List<File> recentFiles = new ArrayList<File>();
    			for(Object o : recentFilePaths) {
                    recentFiles.add(new File(o.toString()));
                }
    			config.setRecentFiles(recentFiles);
    		JSONObject recentDirectories = (JSONObject) jo.get("recentDirectories");
    			config.setRecentLoadDirectory(new File(recentDirectories.get("loadPath").toString()));
    			config.setRecentSaveDirectory(new File(recentDirectories.get("savePath").toString()));;
    		JSONObject preferences = (JSONObject) jo.get("preferences");
	    		for(Object o : preferences.keySet()) {
	    			String key = o.toString();
	                config.getPreferences().put(key, preferences.get(key).toString());
	            }
		}

        if (!config.getPreferences().containsKey(ERROR_BACKGROUND_COLOR)) {
            config.getPreferences().put(ERROR_BACKGROUND_COLOR, "0xFF6666");
        }

        config.getPreferences().put(JD_CORE_VERSION, getJdCoreVersion());

        return config;
    }

    protected String getJdCoreVersion() {
        try {
            Enumeration<URL> enumeration = ConfigurationPersister.class.getClassLoader().getResources("META-INF/MANIFEST.MF");

            while (enumeration.hasMoreElements()) {
                try (InputStream is = enumeration.nextElement().openStream()) {
                    String attribute = new Manifest(is).getMainAttributes().getValue("JD-Core-Version");
                    if (attribute != null) {
                        return attribute;
                    }
                }
            }
        } catch (IOException e) {
            assert ExceptionUtil.printStackTrace(e);
        }

        return "SNAPSHOT";
    }

    @SuppressWarnings("unchecked")
    public void save(Configuration configuration) {
        Point l = configuration.getMainWindowLocation();
        Dimension s = configuration.getMainWindowSize();

        try {
            JSONObject jd = new JSONObject();
	    		JSONObject gui = new JSONObject();
	    			JSONObject mainWindow = new JSONObject();
	    				JSONObject location = new JSONObject();
	    					location.put("x", String.valueOf(l.x));
	    					location.put("y", String.valueOf(l.y));
	    				mainWindow.put("location", location);
	    				JSONObject size = new JSONObject();
	    					size.put("w", String.valueOf(s.width));
	    					size.put("h", String.valueOf(s.height));
	    				mainWindow.put("size", size);
	    				mainWindow.put("maximize", String.valueOf(configuration.isMainWindowMaximize()));
	    			gui.put("mainWindow", mainWindow);
	    		jd.put("gui", gui);
	    		JSONArray recentFilePaths = new JSONArray();
	        		for (File recentFile : configuration.getRecentFiles()) {
	                    recentFilePaths.add(recentFile.toString());
	                }
	        	jd.put("recentFilePaths", recentFilePaths);
	    		JSONObject recentDirectories = new JSONObject();
	    			recentDirectories.put("loadPath", configuration.getRecentLoadDirectory().getAbsolutePath());
	    			recentDirectories.put("savePath", configuration.getRecentSaveDirectory().getAbsolutePath());
	    		jd.put("recentDirectories", recentDirectories);
	    		JSONObject preferences = new JSONObject();
		    		for (Map.Entry<String, String> preference : configuration.getPreferences().entrySet()) {
		                preferences.put(preference.getKey(), preference.getValue());
		            }
		    	jd.put("preferences", preferences);
		    
		    cfg.saveJDConfig(jd);
        } catch (Exception e) {
            assert ExceptionUtil.printStackTrace(e);
        }
    }
}
