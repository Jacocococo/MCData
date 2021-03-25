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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jd.gui.controller.MainController;
import org.jd.gui.model.configuration.Configuration;
import org.jd.gui.service.ConfigurationPersister;

import com.jacoco.mcdata.files.Config;

public class JDGui {
    protected MainController controller;
    protected JPanel parent;
    protected Configuration configuration;

	public JDGui(JPanel parent, Config cfg) {
		this.parent = parent;
        // Load preferences
        ConfigurationPersister persister = new ConfigurationPersister(cfg);
        this.configuration = persister.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> persister.save(configuration)));
	}
	
	public void show() {
        new Thread(
	        () -> {
	        	try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
	        	// Create main controller and show main frame
	            controller = new MainController(parent, configuration);
	            controller.show(Collections.emptyList());
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
