package com.jacoco.mcdata.view;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jacoco.mcdata.Theme;

public class JDView extends View {

	private JPanel panel;
	
	public JDView(Theme thm) {
		this.panel = new JPanel();
		panel.setLayout(new BorderLayout());
		applyTheme(panel, thm, NORMAL);
	}
	
	public JPanel getPanel() {
		return this.panel;
	}

	public List<JComponent> getComponents() {
		return Collections.emptyList();
	}
}
