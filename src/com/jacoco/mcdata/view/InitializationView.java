package com.jacoco.mcdata.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jacoco.mcdata.Theme;
import com.jacoco.mcdata.files.Config;

public class InitializationView extends View {

	private List<JComponent> components = new ArrayList<JComponent>();
	
	private JPanel panel;
	
	private JTextField versionChooser;
	private JLabel versionChooserLabel;
	
	private JTextField exportPath;
	private JLabel exportPathLabel;
	
	private JButton thm;
	
	private Theme theme;
	
	public InitializationView(
			Config cfg, 
			MouseAdapter chooseVersion, 
			MouseAdapter selectExportPath, 
			ActionListener themeAction) {
		this.theme = cfg.getTheme();
		this.panel = new JPanel();
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 0, 0, 0);
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(0, 150));
		applyTheme(panel, theme, NORMAL);
		
		versionChooserLabel = new JLabel("Choose Version Folder");
		applyTheme(versionChooserLabel, theme, NORMAL);
		versionChooserLabel.setPreferredSize(new Dimension(400, 10));
		panel.add(versionChooserLabel, constraints);
		components.add(versionChooserLabel);
		
		versionChooser = new JTextField("Choose Folder");
		applyTheme(versionChooser, theme, NORMAL);
		versionChooser.setPreferredSize(new Dimension(400, 30));
		constraints.gridy = 1;
		panel.add(versionChooser, constraints);
		versionChooser.addMouseListener(chooseVersion);
		components.add(versionChooser);
		
		exportPathLabel = new JLabel("Choose Export Folder");
		applyTheme(exportPathLabel, theme, NORMAL);
		exportPathLabel.setPreferredSize(new Dimension(400, 10));
		constraints.gridy = 2;
		panel.add(exportPathLabel, constraints);
		components.add(exportPathLabel);
				
		exportPath = new JTextField(cfg.getExportPath().toString());
		applyTheme(exportPath, theme, NORMAL);
		exportPath.setPreferredSize(new Dimension(400, 30));
		constraints.gridy = 3;
		panel.add(exportPath, constraints);
		exportPath.addMouseListener(selectExportPath);
		components.add(exportPath);
		
		thm = new JButton(this.theme.getName());
		applyTheme(thm, theme, BUTTON);
		thm.setPreferredSize(new Dimension(100, 30));
		panel.add(thm, constraints);
		thm.addActionListener(themeAction);
		components.add(thm);
	}
	
	public List<JComponent> getComponents() {
		return this.components;
	}
	
	public JPanel getPanel() {
		return this.panel;
	}
}
