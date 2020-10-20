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
import java.nio.file.Paths;

import javax.swing.JFileChooser;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.files.ExportPath;
import com.jacoco.mcdata.files.MapLocation;

public class Gui {

	public static Frame gui;

	private static Dimension dim;
	private static int y;
	
	private Color background;
	private Color text;
	private Color button;
	
	private Button cls;
	
	private Label feLabel;
	private TextField fe;
	
	private Label epLabel;
	public static TextField ep;
	
	private Button thm;
	
	private Button exportJar;
		
	public Gui(int br, int bg, int bb, int tr, int tg, int tb, String mode) throws IOException, InterruptedException {

		setColors(br, bg, bb, tr, tg, tb);

		gui = new Frame(Strings.name);

		// coordinates
		int sizeX = 600;
		int sizeY = 225;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = screenSize.width / 2 - sizeX / 2;
		int centerY = screenSize.height / 2 - sizeY / 2 - 25;

		gui.setSize(sizeX, sizeY);

		// add "close" button
		cls = new Button(Strings.close);
		cls.setBounds(gui.getWidth() / 2 + 10, gui.getHeight() / 7 * 5, gui.getWidth()/6, 30);
		cls.setForeground(text);
		cls.setBackground(button);
		gui.add(cls);
		cls.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});

		/*
		 * file explorer
		 */

		// label
		feLabel = new Label("Choose Version Folder");
		feLabel.setForeground(text);
		feLabel.setLocation(gui.getWidth() / 12, gui.getHeight() / 4 - 20);
		feLabel.setSize(500, 20);
		gui.add(feLabel);

		// text field
		Path tmpDir = Files.createTempDirectory(Strings.name + "_");

		fe = new TextField(Strings.file);
		fe.setBounds(gui.getWidth() / 12, gui.getHeight() / 4, gui.getWidth() / 6 * 5, 30);
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
				try {
					FileDialog fd = new FileDialog(gui, Strings.file, FileDialog.LOAD);
					fd.setVisible(true);
					fe.setText(fd.getDirectory());
					int fel = fe.getText().length();
					fe.setText(fd.getDirectory().substring(0, fel - 1));
					new MapLocation(Paths.get(fe.getText()), tmpDir);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (NullPointerException e2) {}
			}
		});

		/*
		 * export path
		 */

		// label
		epLabel = new Label("Choose Export Folder");
		epLabel.setForeground(text);
		epLabel.setLocation(gui.getWidth() / 12, gui.getHeight() / 2 - 20);
		epLabel.setSize(500, 20);
		gui.add(epLabel);

		// text field
		y = 0;

		if (mode.equals(Strings.light))
			y++;

		ep = new TextField(Config.export);
		ep.setBounds(gui.getWidth() / 12, gui.getHeight() / 2 + y, gui.getWidth() / 6 * 4, 30);
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
				try{
					JFileChooser fd = new JFileChooser(Strings.file);
					fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fd.showOpenDialog(gui);
					ep.setText(fd.getSelectedFile().toString());
					ExportPath.setExportPath(fd.getSelectedFile().toString());
				} catch (NullPointerException e1) {}
				
			}
		});

		// jar export button
		exportJar = new Button("Export");
		exportJar.setBounds(gui.getWidth() / 3 - 10, gui.getHeight() / 7 * 5, gui.getWidth()/6, 30);
		exportJar.setForeground(text);
		exportJar.setBackground(button);
		gui.add(exportJar);
		exportJar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ExportPath.setExportPath(ep.getText());
				ExportPath.exportDirPath = Paths.get(ep.getText());
				try {
					Deobfuscation.export();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		// dark mode
		thm = new Button(mode);
		thm.setBounds(gui.getWidth() / 4 * 3, gui.getHeight() / 2, gui.getWidth()/6, 30);
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
						setColors(0, 0, 50, 254, 254, 254);
						updateColors();
						break;
	
					// is dark
					case Strings.dark:
						thm.setLabel(Strings.light);
						DarkMode.setMode(Strings.light);
						setColors(254, 254, 254, 0, 0, 0);
						updateColors();
						break;
				}
			}
		});
		
		// make application work
		gui.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});

		gui.setLocation(centerX, centerY);
		gui.setBackground(background);
		gui.setLayout(null);
		gui.setVisible(true);
		
		dim = gui.getSize();
		
		while(true) {
			System.out.print("");
			if(!dim.equals(gui.getSize())) {
				
				cls.setBounds(gui.getWidth() / 2 + 10, gui.getHeight() / 7 * 5, gui.getWidth()/6, 30);
				
				exportJar.setBounds(gui.getWidth() / 3 - 10, gui.getHeight() / 7 * 5, gui.getWidth()/6, 30);
				
				fe.setBounds(gui.getWidth() / 12, gui.getHeight() / 4, gui.getWidth() / 6 * 5, 30);
				
				feLabel.setLocation(gui.getWidth() / 12, gui.getHeight() / 4 - 20);
				feLabel.setSize(500, 20);
				
				ep.setBounds(gui.getWidth() / 12, gui.getHeight() / 2 + y, gui.getWidth() / 6 * 4, 30);
				
				epLabel.setLocation(gui.getWidth() / 12, gui.getHeight() / 2 - 20);
				epLabel.setSize(500, 20);
				
				thm.setBounds(gui.getWidth() / 4 * 3, gui.getHeight() / 2, gui.getWidth()/6, 30);
				
				dim = gui.getSize();
			}
		}
	}
	
	private void setColors(int br, int bg, int bb, int tr, int tg, int tb) {
		background = new Color(br, bg, bb);
		text = new Color(tr, tg, tb);

		int btnr = 0, btng = 0, btnb = 0;
		int chg = 35;

		if (br >= 100) {
			btnr = br - chg;
			btng = bg - chg;
			btnb = bb - chg;
		} else if (br <= 100) {
			btnr = br + chg;
			btng = bg + chg;
			btnb = bb - 20 + chg;
		}

		button = new Color(btnr, btng, btnb);
	}
	
	private void updateColors() {
		cls.setForeground(text);
		cls.setBackground(button);
		
		feLabel.setForeground(text);
		feLabel.setBackground(background);
		
		fe.setForeground(text);
		fe.setBackground(background);
		
		epLabel.setForeground(text);
		epLabel.setBackground(background);
		
		ep.setForeground(text);
		ep.setBackground(background);
		
		exportJar.setForeground(text);
		exportJar.setBackground(button);
		
		thm.setForeground(text);
		thm.setBackground(button);
		
		gui.setBackground(background);
	}
	
	private void onClose() {
		ExportPath.setExportPath(ep.getText());
		System.exit(0);
	}
}
