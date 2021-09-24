package com.jacoco.mcdata.controller;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;

import org.jd.gui.JDGui;

import com.jacoco.mcdata.files.Config;

public class JDController {

	private JDGui jd;
	private JPanel view;
	
	private boolean visible = false;
	private Consumer<JPanel> onViewShown;
	private Consumer<JPanel> onViewHidden;
	
	public JDController(Config cfg, Consumer<JPanel> onViewShown, Consumer<JPanel> onViewHidden) {
		this.onViewShown = onViewShown;
		this.onViewHidden = onViewHidden;
		
		this.view = new JPanel();
		view.setLayout(new BorderLayout());
		this.jd = new JDGui(view, cfg, this::toggleVisible);
		jd.show();
	}
	
	public JPanel getView() {
		return this.view;
	}
	
	public void loadFile(File file) {
		this.jd.loadFile(file);
	}
	
	public void loadFiles(List<File> files) {
		this.jd.loadFiles(files);
	}
	
	public void toggleVisible() {
		if(visible) {
			onViewHidden.accept(this.view);
			visible = false;
		} else {
			onViewShown.accept(this.view);
			visible = true;
		}
			
	}
}
