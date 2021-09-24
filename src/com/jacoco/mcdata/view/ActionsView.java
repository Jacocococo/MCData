package com.jacoco.mcdata.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jacoco.mcdata.files.Config;

public class ActionsView extends JPanel {

	public ActionsView(
			Config cfg, 
			ActionListener exportListener, 
			ActionListener closeListener) {
		
		Dimension buttonSize = new Dimension(100, 40);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0, 10, 0, 10);
		constraints.fill = GridBagConstraints.NONE;
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(0, 100));
		
		JButton export = new JButton("Export");
		export.setPreferredSize(buttonSize);
		add(export, constraints);
		export.addActionListener(exportListener);
		
		JButton close = new JButton("Close");
		close.setPreferredSize(buttonSize);
		add(close, constraints);
		close.addActionListener(closeListener);
	}
}
