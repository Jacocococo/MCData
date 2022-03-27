package com.jacoco.mcdata.controller;

import javax.swing.JPanel;

import com.jacoco.mcdata.view.ProgressView;

public class ProgressController {

	private static final ProgressController INSTACE = new ProgressController();
	
	public static ProgressController getInstance() {
		return INSTACE;
	}
	
	private ProgressController() {}
	
	private ProgressView view = new ProgressView();
	private int count = 0;
	private boolean isFinished = false;
	
	public JPanel getView() {
		return this.view;
	}
	
	public void newProgress(int stepCount) {
		this.view.reset();
		this.view.setMaximum(stepCount);
		this.count = 0;
		this.isFinished = false;
	}
	
	public void step(int amount) {
		if(this.isFinished)
			return;
		this.count += amount;
		this.view.setValue(count);
	}
	
	public void step() {
		this.step(1);
	}
	
	public void increaseMaximum(int value) {
		this.view.setMaximum(this.view.getMaximum() + value);
		if(this.view.getMaximum() < this.view.getValue() && this.isFinished)
			this.isFinished = false;
	}
	
	public void finishProgress() {
		if(this.view.getMaximum() > this.view.getValue())
			this.view.finish();
		this.isFinished = true;
	}
}
