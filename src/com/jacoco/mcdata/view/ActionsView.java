package com.jacoco.mcdata.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jacoco.mcdata.Theme;
import com.jacoco.mcdata.files.Config;

public class ActionsView extends View {

	private JPanel panel;
	private Theme theme;
	private List<JComponent> components = new ArrayList<JComponent>();
	
	public ActionsView(
			Config cfg, 
			ActionListener exportListener, 
			ActionListener closeListener) {
		this.theme = cfg.getTheme();
		this.panel = new JPanel();
		
		Dimension buttonSize = new Dimension(100, 40);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 10, 0, 10);
		constraints.fill = GridBagConstraints.NONE;
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(0, 100));
		applyTheme(panel, theme, NORMAL);
		
		JButton export = new JButton("Export");
		export.setPreferredSize(buttonSize);
		applyTheme(export, theme, BUTTON);
		panel.add(export, constraints);
		export.addActionListener(exportListener);
		components.add(export);
		
		JButton close = new JButton("Close");
		close.setPreferredSize(buttonSize);
		applyTheme(close, theme, BUTTON);
		panel.add(close, constraints);
		close.addActionListener(closeListener);
		components.add(close);
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public List<JComponent> getComponents() {
		return components;
	}
}
