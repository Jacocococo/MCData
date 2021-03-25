package com.jacoco.mcdata.view;

import java.awt.Color;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jacoco.mcdata.Theme;

public abstract class View {

	public abstract JPanel getPanel();
	public abstract List<JComponent> getComponents();
	
	protected final String NORMAL = "normal";
	protected final String BUTTON = "button";
	
	public void updateTheme(Theme theme) {
		Color text = theme.getText();
		Color background = theme.getBg();
		Color button = theme.getBtn();
		getComponents().forEach(e -> {
			switch(e.getName()) {
				case NORMAL:
					e.setForeground(text);
					e.setBackground(background);
					break;
				case BUTTON:
					e.setForeground(text);
					e.setBackground(button);
					break;
			}
		});
	}
	
	protected void applyTheme(JComponent component, Theme theme, String type) {
		Color text = theme.getText();
		Color background = theme.getBg();
		Color button = theme.getBtn();
		component.setForeground(text);
		if(type.equals(NORMAL))
			component.setBackground(background);
		else if(type.equals(BUTTON))
			component.setBackground(button);
		component.setName(type);
	}
}