package com.jacoco.mcdata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
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
	
	private Deobfuscation deobf;
	
	private List<View> views = new ArrayList<View>();
		
	public Gui(Config cfg) {
		Theme theme = cfg.getTheme();
		this.deobf = new Deobfuscation(MappingFormat.PROGUARD, EnigmaProfile.EMPTY);
		
		this.frame = new JFrame("MCData");

		// coordinates
		Dimension size = new Dimension(1000, 800);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = screenSize.width / 2 - size.width / 2;
		int centerY = screenSize.height / 2 - size.height / 2 - 25;

		this.layout = new BorderLayout();
		frame.setLayout(this.layout);
		
		frame.setLocation(centerX, centerY);
		frame.setSize(size);
		frame.setMinimumSize(size);
		frame.setBackground(theme.getBg());
		
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
		JDController jdController = new JDController(cfg);
		addView(jdController.getView(), BorderLayout.CENTER);
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
		this.frame.add(view.getPanel(), constraint);
	}
	
	public JFrame getFrame() {
		return this.frame;
	}
}