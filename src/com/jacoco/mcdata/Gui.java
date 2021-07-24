package com.jacoco.mcdata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jacoco.mcdata.controller.ActionsController;
import com.jacoco.mcdata.controller.InitializationController;
import com.jacoco.mcdata.controller.JDController;
import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.files.Deobfuscation;
import com.jacoco.mcdata.view.View;

import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;

public class Gui {

	private JFrame frame;
	private BorderLayout layout;
	private final Dimension DEFAULT_SIZE = new Dimension(1000, 800);
	
	private Deobfuscation deobf;
	
	private List<View> views = new ArrayList<View>();
	
	public Gui(Config cfg) {
		Theme theme = cfg.getTheme();
		this.deobf = new Deobfuscation(MappingFormat.PROGUARD, EnigmaProfile.EMPTY);
		this.frame = new JFrame("MCData");

		this.layout = new BorderLayout();
		frame.setLayout(this.layout);
		
		frame.setSize(DEFAULT_SIZE);
		frame.setMinimumSize(DEFAULT_SIZE);
		frame.setLocationRelativeTo(null);
		frame.setBackground(theme.getBg());
		
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
		west.setBackground(theme.getBg());
		east.setBackground(theme.getBg());
		
		InitializationController initController = new InitializationController(cfg, this.views);
		addView(initController.getView(), BorderLayout.NORTH);
		frame.add(west, BorderLayout.WEST);
		JDController jdController = new JDController(cfg,
				view -> addView(view, BorderLayout.CENTER),
				view -> removeView(view, true));
		frame.add(east, BorderLayout.EAST);
		ActionsController actionsController = new ActionsController(initController, jdController, cfg, deobf, this::onClose);
		addView(actionsController.getView(), BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	private void onClose() {
		System.exit(0);
	}
	
	public void addView(View view, String constraint) {
		views.add(view);
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
		this.frame.add(view.getPanel(), constraint);
	}
	
	public void removeView(View view, boolean isCenter) {
		views.remove(view);
		this.frame.remove(view.getPanel());
		if(isCenter && this.frame.getSize().equals(DEFAULT_SIZE)) {
			Point currentLoc = this.frame.getLocation();
			Dimension size = new Dimension(650, 275);
			frame.setMinimumSize(size);
			frame.setSize(size);
			this.frame.setLocation((int) (currentLoc.getX() + (DEFAULT_SIZE.getWidth() - size.getWidth()) / 2),
								   (int) (currentLoc.getY() + (DEFAULT_SIZE.getHeight() - size.getHeight()) / 2));
		}
		this.frame.revalidate();
		this.frame.repaint();
	}
	
	public void removeView(View view) {
		this.removeView(view, false);
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
}