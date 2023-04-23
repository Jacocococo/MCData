package com.jacoco.mcdata.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;

import javax.swing.JTextField;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.version.Version;
import com.jacoco.mcdata.version.Versions;
import com.jacoco.mcdata.view.InitializationView;
import com.jacoco.nfd.NativeFileDialog;

public class InitializationController {

	private Config cfg;
	
	private InitializationView view;
	
	public InitializationController(Config cfg) {
		this.cfg = cfg;
		
		this.view = new InitializationView(
				cfg,
				chooseVersion,
				selectExportPath
			);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> cfg.setExportPath(this.view.getExportPath())));
	}
	
	public InitializationView getView() {
		return this.view;
	}
	
	public String getExportPath() {
		return this.view.getExportPath();
	}
	
	private MouseAdapter chooseVersion = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			String path = NativeFileDialog.getPath(null);
			if (path == null)
				return;
			((JTextField) e.getComponent()).setText(path);
			Version version = new Version(Paths.get(path));
			Versions.removeIfUnused(Versions.getCurrent());
			Versions.add(version);
		}
	};
	
	private MouseAdapter selectExportPath = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			String path = NativeFileDialog.getPath(null);
			if (path == null)
				return;
			((JTextField) e.getComponent()).setText(path);
			cfg.setExportPath(path);
		}
	};
}
