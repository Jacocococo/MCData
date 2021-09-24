package com.jacoco.mcdata.controller;

import java.awt.FileDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.version.Version;
import com.jacoco.mcdata.version.Versions;
import com.jacoco.mcdata.view.InitializationView;

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
			try {
				FileDialog fd = new FileDialog(new JFrame(), "Choose Folder");
				fd.setVisible(true);
				Path path = Paths.get(fd.getDirectory());
				((JTextField) e.getComponent()).setText(path.toString());
				Versions.add(new Version(path));
			} catch (NullPointerException ex) {}
		}
	};
	
	private MouseAdapter selectExportPath = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			try {
				JFileChooser fd = new JFileChooser("Choose File");
			    fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    fd.showOpenDialog(view);
				((JTextField) e.getComponent()).setText(fd.getSelectedFile().toString());
				cfg.setExportPath(fd.getSelectedFile().toString());
			} catch (NullPointerException ex) {}
		}
	};
}
