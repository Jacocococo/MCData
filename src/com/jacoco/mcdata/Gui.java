package com.jacoco.mcdata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jacoco.mcdata.controller.ActionsController;
import com.jacoco.mcdata.controller.InitializationController;
import com.jacoco.mcdata.controller.JDController;
import com.jacoco.mcdata.controller.ProgressController;
import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.files.Deobfuscation;

import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;

public class Gui {

	private JFrame frame;
	private JPanel mainPanel;
	private final Dimension DEFAULT_SIZE = new Dimension(1000, 800);
	
	public Gui(Config cfg) {
		this.frame = new JFrame("MCData");
		this.mainPanel = new JPanel();

		frame.setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(1920, 1080));
		
		frame.setSize(DEFAULT_SIZE);
		frame.setMinimumSize(DEFAULT_SIZE);
		frame.setLocationRelativeTo(null);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				Gui.this.onClose();
			}
		});
		
		Dimension prefSize = new Dimension(200, 0);
		JPanel west = new JPanel();
		JPanel east = new JPanel();
		west.setPreferredSize(prefSize);
		east.setPreferredSize(prefSize);

		InitializationController initController = new InitializationController(cfg);
		addView(initController.getView(), BorderLayout.NORTH);
		mainPanel.add(west, BorderLayout.WEST);
		JDController jdController = new JDController(cfg,
				view -> addView(view, BorderLayout.CENTER),
				view -> removeView(view, true));
		mainPanel.add(east, BorderLayout.EAST);
		ActionsController actionsController = new ActionsController(initController, jdController, cfg, this::onClose);
		addView(actionsController.getView(), BorderLayout.SOUTH);
		
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.add(ProgressController.getInstance().getView(), BorderLayout.SOUTH);
	}

	public void show() {
		frame.setVisible(true);
	}
	
	private void onClose() {
		System.exit(0);
	}
	
	public void addView(JPanel view, String constraint) {
		Point currentLoc = this.frame.getLocation();
		Dimension currentSize = this.frame.getSize();
		if(currentSize.getWidth() < DEFAULT_SIZE.getWidth())
			if(currentSize.getHeight() < DEFAULT_SIZE.getHeight()) {
				this.frame.setLocation((int) (currentLoc.getX() - (DEFAULT_SIZE.getWidth() - currentSize.getWidth()) / 2),
									   (int) (currentLoc.getY() - (DEFAULT_SIZE.getHeight() - currentSize.getHeight()) / 2));
				this.frame.setSize(DEFAULT_SIZE);
			} else {
				this.frame.setLocation((int) (currentLoc.getX() - (DEFAULT_SIZE.getWidth() - currentSize.getWidth()) / 2), (int) currentLoc.getY());
				this.frame.setSize((int) DEFAULT_SIZE.getWidth(), (int) currentSize.getHeight());
			}
		else if(currentSize.getHeight() < DEFAULT_SIZE.getHeight()) {
			this.frame.setLocation((int) currentLoc.getX(), (int) (currentLoc.getY() - (DEFAULT_SIZE.getHeight() - currentSize.getHeight()) / 2));
			this.frame.setSize((int) currentSize.getWidth(), (int) DEFAULT_SIZE.getHeight());
		}
		if(this.frame.getLocation().getX() < 0)
			this.frame.setLocation(0, (int) this.frame.getLocation().getY());
		if(this.frame.getLocation().getY() < 0)
			this.frame.setLocation((int) this.frame.getLocation().getX(), 0);
		this.mainPanel.add(view, constraint);
	}
	
	public void removeView(JPanel view, boolean isCenter) {
		this.mainPanel.remove(view);
		if(isCenter && this.frame.getSize().equals(DEFAULT_SIZE)) {
			Point currentLoc = this.frame.getLocation();
			Dimension size = new Dimension(650, 300);
			frame.setMinimumSize(size);
			frame.setSize(size);
			this.frame.setLocation((int) (currentLoc.getX() + (DEFAULT_SIZE.getWidth() - size.getWidth()) / 2),
								   (int) (currentLoc.getY() + (DEFAULT_SIZE.getHeight() - size.getHeight()) / 2));
		}
		this.mainPanel.revalidate();
		this.mainPanel.repaint();
	}
	
	public void removeView(JPanel view) {
		this.removeView(view, false);
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
}