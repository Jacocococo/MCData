package com.jacoco.mcdata;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.jacoco.mcdata.files.ExportPath;

public class Gui {

	public static Frame gui;
	
	public static TextField ep;
	
	public Gui(int br, int bg, int bb, int tr, int tg, int tb, String mode) throws IOException {
		
		Color background = new Color(br, bg, bb);
		Color text = new Color(tr, tg, tb);
		
		// button color
		int btnr = 0; int btng = 0; int btnb = 0;
		
		int chg = 35;
		
		if(br >=100) {
			btnr = br-chg;
			btng = bg-chg;
			btnb = bb-chg;
		} else if(br <= 100) {
			btnr = br+chg;
			btng = bg+chg;
			btnb = bb-20+chg;
		}
		
		Color button = new Color(btnr, btng, btnb);

		gui = new Frame(Strings.name);

		// coordinates
		int sizeX = 600;
		int sizeY = 225;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = screenSize.width / 2 - sizeX / 2;
		int centerY = screenSize.height / 2 - sizeY / 2 - 25;

		// add "close" button
		Button cls = new Button(Strings.close);
		cls.setBounds(sizeX/2+5, 165, 100, 30);
		cls.setForeground(text);
		cls.setBackground(button);
		gui.add(cls);
		cls.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}
		});

		/*
		 * file explorer
		 */
		
		// label
		Label feLabel = new Label("Choose Version Folder");
		feLabel.setForeground(text);
		feLabel.setLocation(sizeX / 2 - 500 / 2 - 2, 50);
		feLabel.setSize(500, 10);
		gui.add(feLabel);
		
		// text field
		Path tmpDir = Files.createTempDirectory(Strings.name + "_");

		TextField fe = new TextField(Strings.file);
		fe.setBounds(sizeX / 2 - 500 / 2, 65, 500, 30);
		fe.setForeground(text);
		fe.setBackground(background);
		gui.add(fe);
		fe.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}

			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				FileDialog fd = new FileDialog(gui, Strings.file, FileDialog.LOAD);
				fd.setVisible(true);
				fe.setText(fd.getDirectory());
				int fel = fe.getText().length();
				fe.setText(fd.getDirectory().substring(0, fel - 1));
				try {
					new com.jacoco.mcdata.files.MapLocation(fe.getText(), tmpDir);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		/* 
		 * export path
		 */
		
		// label
		Label epLabel = new Label("Choose Export Folder");
		epLabel.setForeground(text);
		epLabel.setLocation(sizeX / 2 - 500 / 2 - 2, 100);
		epLabel.setSize(500, 10);
		gui.add(epLabel);
		
		// text field
		int y = 115;
		
		if(mode.equals(Strings.light))
			y++;
		
		ep = new TextField();
		ep.setBounds(sizeX / 2 - 500 / 2, y, 400, 30);
		ep.setForeground(text);
		ep.setBackground(background);
		gui.add(ep);
		ep.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}

			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				FileDialog fd = new FileDialog(gui, Strings.file, FileDialog.LOAD);
				fd.setVisible(true);
				ep.setText(fd.getDirectory());
				ExportPath.setExportPath(fd.getDirectory());
			}
		});

		// dark mode
		Button thm = new Button(mode);
		thm.setBounds(sizeX / 2 + 150, 115, 100, 30);
		thm.setForeground(text);
		thm.setBackground(button);
		gui.add(thm);
		thm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (thm.getLabel()) {

				// is light
				case Strings.light:
					thm.setLabel(Strings.dark);
					DarkMode.setMode(Strings.dark);
					break;

				// is dark
				case Strings.dark:
					thm.setLabel(Strings.light);
					DarkMode.setMode(Strings.light);
					break;
				}

				DarkMode.error();
			}
		});

		// jar export button
		Button ExportJar = new Button("Export");
		ExportJar.setBounds(sizeX/2-105, 165, 100, 30);
		ExportJar.setForeground(text);
		ExportJar.setBackground(button);
		gui.add(ExportJar);
		ExportJar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Deobfuscation.export();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// make application work
		gui.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		gui.setLocation(centerX, centerY);
		gui.setBackground(background);
		gui.setSize(sizeX, sizeY);
		gui.setLayout(null);
		gui.setVisible(true);

	}
}