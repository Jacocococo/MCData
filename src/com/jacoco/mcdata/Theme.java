package com.jacoco.mcdata;

import java.awt.Color;

public enum Theme {
	LIGHT("Light Mode", new Color(254, 254, 254), new Color(0, 0, 0)), 
	DARK("Dark Mode", new Color(0, 0, 50), new Color(254, 254, 254));

	private String name;
	private Color bg;
	private Color text;
	private Color btn;

	Theme(String name, Color bg, Color text) {
		this.name = name;
		this.bg = bg;
		this.text = text;
		
		int r = this.bg.getRed(), g = this.bg.getGreen(), b = this.bg.getBlue();
		int chg = 50;

		if (r >= 100) {
			btn = new Color(r - chg, g - chg, b - chg);
		} else if (r <= 100) {
			btn = new Color(r + chg, g + chg, b - 20 + chg);
		}
	}
	
	public static Theme getThemeFromName(String name) {
		switch(name) {
			case "Light Mode":
				return Theme.LIGHT;
			case "Dark Mode":
				return Theme.LIGHT;
			default:
				return Theme.LIGHT;
		}
	}

	public String getName() {
		return this.name;
	}

	public Color getBg() {
		return this.bg;
	}

	public Color getText() {
		return this.text;
	}
	
	public Color getBtn() {
		return this.btn;
	}
}