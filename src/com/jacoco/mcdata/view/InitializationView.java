package com.jacoco.mcdata.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jacoco.mcdata.files.Config;

public class InitializationView extends JPanel {

	private JTextField versionChooser;
	private JLabel versionChooserLabel;
	
	private JTextField exportPath;
	private JLabel exportPathLabel;
	
	public InitializationView(
			Config cfg, 
			MouseAdapter chooseVersion, 
			MouseAdapter selectExportPath) {
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 0, 0, 0);
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(0, 150));
		
		versionChooserLabel = new JLabel("Choose Version Folder");
		versionChooserLabel.setPreferredSize(new Dimension(400, 20));
		add(versionChooserLabel, constraints);
		
		versionChooser = new JTextField("Choose Folder");
		versionChooser.setPreferredSize(new Dimension(400, 30));
		constraints.gridy = 1;
		add(versionChooser, constraints);
		versionChooser.addMouseListener(chooseVersion);
		
		exportPathLabel = new JLabel("Choose Export Folder");
		exportPathLabel.setPreferredSize(new Dimension(400, 20));
		constraints.gridy = 2;
		add(exportPathLabel, constraints);
				
		exportPath = new JTextField(cfg.getExportPath().toString());
		exportPath.setPreferredSize(new Dimension(400, 30));
		constraints.gridy = 3;
		add(exportPath, constraints);
		exportPath.addMouseListener(selectExportPath);
	}
	
	public String getExportPath() {
		return this.exportPath.getText();
	}
}
