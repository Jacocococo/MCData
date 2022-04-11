package com.jacoco.mcdata.controller;

import java.awt.event.ActionListener;
import java.nio.file.Path;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.files.Deobfuscation;
import com.jacoco.mcdata.version.Versions;
import com.jacoco.mcdata.view.ActionsView;

import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;

public class ActionsController {

	private InitializationController initController;
	private JDController jdController;
	private Config cfg;
	private Deobfuscation deobf;
	private Runnable close;
	
	private ActionsView view;

	public ActionsController(InitializationController initController,
			JDController jdController,
			Config cfg,
			Runnable close) {
		this.initController = initController;
		this.jdController = jdController;
		this.cfg = cfg;
		this.close = close;
		
		this.deobf = new Deobfuscation(MappingFormat.PROGUARD, EnigmaProfile.EMPTY);;
		
		this.view = new ActionsView(cfg, exportListener, e -> this.close.run());
	}
	
	public ActionsView getView() {
		return this.view;
	}

	private ActionListener exportListener = e -> {
		cfg.setExportPath(initController.getExportPath());
		Path exportPath = cfg.getExportPath();
		deobf.addOnFinishEvent(() -> {
			 jdController.loadFile(exportPath.resolve(Versions.getCurrent().getExportedJar()).toFile());
		});
		deobf.export(exportPath, Versions.getCurrent());
	};
}
