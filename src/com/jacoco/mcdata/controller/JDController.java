package com.jacoco.mcdata.controller;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import org.jd.gui.JDGui;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.view.JDView;
import com.jacoco.mcdata.view.View;

public class JDController {

	private JDView view;
	private JDGui jd;
	
	private boolean visible = false;
	private Consumer<View> onViewShown;
	private Consumer<View> onViewHidden;
	
	public JDController(Config cfg, Consumer<View> onViewShown, Consumer<View> onViewHidden) {
		this.onViewShown = onViewShown;
		this.onViewHidden = onViewHidden;
		
		this.view = new JDView(cfg.getTheme());
		this.jd = new JDGui(view.getPanel(), cfg, this::toggleVisible);
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
