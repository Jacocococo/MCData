package com.jacoco.mcdata.controller;

import java.io.File;
import java.util.List;

import org.jd.gui.JDGui;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.view.JDView;

public class JDController {

	private JDView view;
	private JDGui jd;
	
	public JDController(Config cfg) {
		this.view = new JDView(cfg.getTheme());
		this.jd = new JDGui(view.getPanel(), cfg);
		jd.show();
	}
	
	public JDView getView() {
		return this.view;
	}
	
	public void loadFile(File file) {
		this.jd.loadFile(file);
	}
	
	public void loadFiles(List<File> files) {
		this.jd.loadFiles(files);
	}
}
