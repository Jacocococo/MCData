package com.jacoco.mcdata.view;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressView extends JPanel {

	private JProgressBar progressBar;
	
	public ProgressView() {
		this.progressBar = new JProgressBar();
		setLayout(new GridLayout());
		progressBar.setPreferredSize(new Dimension(0, 25));
		progressBar.setMaximum(0);
		add(progressBar);
	}
	
	public void setValue(int i) {
		progressBar.setValue(i);
	}
	
	public int getValue() {
		return progressBar.getValue();
	}
	
	public void setMaximum(int max) {
		progressBar.setMaximum(max);
	}
	
	public int getMaximum() {
		return progressBar.getMaximum();
	}
	
	public void reset() {
		progressBar.setValue(0);
	}
	
	public void finish() {
		progressBar.setValue(progressBar.getMaximum());
	}
}
