/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.gui;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.jd.gui.controller.MainController;
import org.jd.gui.model.configuration.Configuration;
import org.jd.gui.service.ConfigurationPersister;

import com.jacoco.mcdata.files.Config;

public class JDGui {
    protected MainController controller;
    protected JPanel parent;
    protected Configuration configuration;
	private Runnable toggleVisibility;

	public JDGui(JPanel parent, Config cfg, Runnable toggleVisibility) {
		this.parent = parent;
		this.toggleVisibility = toggleVisibility;
        // Load preferences
        ConfigurationPersister persister = new ConfigurationPersister(cfg);
        this.configuration = persister.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> persister.save(configuration)));
	}
	
	public void show() {
        new Thread(
	        () -> {
	            controller = new MainController(parent, configuration, toggleVisibility);
	            controller.show(Collections.emptyList());
	            toggleVisibility.run();
	        },
	        "JD-Gui"
        ).start();
	}
	
	public void loadFile(File file) {
		controller.openFile(file);
	}
	
	public void loadFiles(List<File> files) {
		controller.openFiles(files);
	}
}
