package com.jacoco.mcdata.controller;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jacoco.mcdata.Theme;
import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.version.Version;
import com.jacoco.mcdata.version.Versions;
import com.jacoco.mcdata.view.InitializationView;
import com.jacoco.mcdata.view.View;

public class InitializationController {

	private Config cfg;
	
	private InitializationView view;
	private JPanel panel;
	private List<JComponent> components;
	
	private List<View> views;
	
	public InitializationController(Config cfg, List<View> views) {
		this.cfg = cfg;
		this.views = views;
		
		this.view = new InitializationView(
				cfg,
				chooseVersion,
				selectExportPath,
				this::themeAction
			);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> cfg.setExportPath(((JTextField) view.getComponents().get(3)).getText())));
		
		this.panel = view.getPanel();
		this.components = view.getComponents();
	}
	
	public InitializationView getView() {
		return this.view;
	}
	
	public String getExportPath() {
		return ((JTextField) this.components.get(3)).getText();
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
				JFileChooser fd = null;
			    LookAndFeel previousLF = UIManager.getLookAndFeel();
			    try {
			        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			        fd = new JFileChooser("Choose File");
			        UIManager.setLookAndFeel(previousLF);
			    } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException ex) {}
			    fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    fd.showOpenDialog(panel);
				((JTextField) e.getComponent()).setText(fd.getSelectedFile().toString());
				cfg.setExportPath(fd.getSelectedFile().toString());
			} catch (NullPointerException ex) {}
		}
	};
	
	private void themeAction(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		
		String light = Theme.LIGHT.getName();
		String dark = Theme.DARK.getName();
		
		switch(((JButton) e.getSource()).getText()) {
			case "Light Mode":
				btn.setText(dark);
				cfg.setTheme(dark);
				for(View guiView : this.views) guiView.updateTheme(Theme.DARK);
				break;
			case "Dark Mode":
				btn.setText(light);
				cfg.setTheme(light);
				for(View guiView : this.views) guiView.updateTheme(Theme.LIGHT);
				break;
		}
	}
}
