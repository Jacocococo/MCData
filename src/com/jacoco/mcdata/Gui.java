package com.jacoco.mcdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jd.gui.JDGui;

import com.jacoco.mcdata.files.Config;
import com.jacoco.mcdata.files.Deobfuscation;

import cuchaz.enigma.EnigmaProfile;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;

public class Gui {

	private JFrame gui;
	private JPanel initPanel;
	private BorderLayout layout;
	
	private Config cfg;
	private Theme theme;
	private Deobfuscation deobf;
	
	private Color background;
	private Color text;
	private Color button;
		
	private JLabel versionChooserLabel;
	private JTextField versionChooser;
	
	private JLabel exportPathLable;
	private JTextField exportPath;
	
	private JDGui jd;
	
	private JButton thm;
	
	private JButton exportJar;
	
	private JButton cls;

	public Gui(Theme theme, Config cfg) {

		this.theme = theme;
		this.cfg = cfg;
		this.deobf = new Deobfuscation(this, EnigmaProfile.EMPTY, MappingFormat.PROGUARD);
		
		this.background = this.theme.getBg();
		this.text = this.theme.getText();
		this.button = this.theme.getBtn();
		
		this.gui = new JFrame("MCData");
		this.initPanel = new JPanel();

		// coordinates
		int sizeX = 600;
		int sizeY = 225;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = screenSize.width / 2 - sizeX / 2;
		int centerY = screenSize.height / 2 - sizeY / 2 - 25;

		this.layout = new BorderLayout();
		gui.setLayout(this.layout);
		
		gui.setLocation(centerX, centerY);
		gui.setSize(sizeX, sizeY);
		
		// add the init panel
		initPanel.setLayout(null);
		initPanel.setBackground(this.background);

		/*
		 * version chooser
		 */

		// label
		versionChooserLabel = new JLabel("Choose Version Folder");
		versionChooserLabel.setForeground(text);
		versionChooserLabel.setLocation(initPanel.getWidth() / 12, initPanel.getHeight() / 4 - 20);
		versionChooserLabel.setSize(500, 20);
		initPanel.add(versionChooserLabel);

		// text field
		versionChooser = new JTextField("Choose Folder");
		versionChooser.setBounds(initPanel.getWidth() / 12, initPanel.getHeight() / 4, initPanel.getWidth() / 6 * 5, 30);
		versionChooser.setForeground(text);
		versionChooser.setBackground(background);
		initPanel.add(versionChooser);
		versionChooser.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				FileDialog fd = new FileDialog(gui, "Choose Folder", FileDialog.LOAD);
				fd.setVisible(true);
				Path path = Paths.get(fd.getDirectory());
				versionChooser.setText(path.toString());

				Versions.add(new Version(path));
			}
		});

		/*
		 * export path
		 */

		// label
		exportPathLable = new JLabel("Choose Export Folder");
		exportPathLable.setForeground(text);
		exportPathLable.setLocation(initPanel.getWidth() / 12, initPanel.getHeight() / 2 - 20);
		exportPathLable.setSize(500, 20);
		initPanel.add(exportPathLable);

		// text field
		exportPath = new JTextField(cfg.getExportPath().toString());
		exportPath.setBounds(initPanel.getWidth() / 12, initPanel.getHeight() / 2, initPanel.getWidth() / 6 * 4, 30);
		exportPath.setForeground(text);
		exportPath.setBackground(background);
		initPanel.add(exportPath);
		exportPath.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fd = new JFileChooser("Choose Folder");
				fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fd.showOpenDialog(initPanel);
				exportPath.setText(fd.getSelectedFile().toString());
				cfg.setExportPath(fd.getSelectedFile().toString());
			}
		});
		
		// jd-gui
		jd = new JDGui();

		// jar export button
		exportJar = new JButton("Export");
		exportJar.setBounds(initPanel.getWidth() / 3 - 10, initPanel.getHeight() / 7 * 5, initPanel.getWidth()/6, 30);
		exportJar.setForeground(text);
		exportJar.setBackground(button);
		initPanel.add(exportJar);
		exportJar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cfg.setExportPath(exportPath.getText());
				Path exportPath = cfg.getExportPath();
				deobf.addOnFinishEvent(() -> {
					jd.loadFile(exportPath.resolve(Versions.getLatest().getExportedJar()).toFile());
				});
				deobf.export(exportPath, Versions.getLatest());
			}
		});
		
		// close button
		cls = new JButton("Close");
		cls.setBounds(initPanel.getWidth() / 2 + 10, initPanel.getHeight() / 7 * 5, initPanel.getWidth()/6, 30);
		cls.setForeground(text);
		cls.setBackground(button);
		initPanel.add(cls);
		cls.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		
		// theme
		thm = new JButton(this.theme.getName());
		thm.setBounds(initPanel.getWidth() / 4 * 3, initPanel.getHeight() / 2, initPanel.getWidth()/6, 30);
		thm.setForeground(text);
		thm.setBackground(button);
		initPanel.add(thm);
		thm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Theme theme;
				
				String light = "Light Mode";
				String dark = "Dark Mode";
				
				switch (thm.getText()) {
					case "Light Mode":
						thm.setText(dark);
						cfg.setTheme(dark);
						theme = Theme.DARK;
						setTheme(theme);
						updateColors();
						break;
	
					case "Dark Mode":
						thm.setText(light);
						cfg.setTheme(light);
						theme = Theme.LIGHT;
						setTheme(theme);
						updateColors();
						break;
				}
			}
		});
				
		// make application work
		gui.setBackground(background);
		gui.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				onClose();
			}
		});
		gui.setDefaultCloseOperation(3);
		gui.setVisible(true);
		
		initPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				cls.setBounds(initPanel.getWidth() / 2 + 10, initPanel.getHeight() / 7 * 5, initPanel.getWidth()/6, 30);
				
				exportJar.setBounds(initPanel.getWidth() / 3 - 10, initPanel.getHeight() / 7 * 5, initPanel.getWidth()/6, 30);
				
				versionChooser.setBounds(initPanel.getWidth() / 12, initPanel.getHeight() / 4, initPanel.getWidth() / 6 * 5, 30);
				
				versionChooserLabel.setLocation(initPanel.getWidth() / 12, initPanel.getHeight() / 4 - 20);
				versionChooserLabel.setSize(500, 20);
				
				exportPath.setBounds(initPanel.getWidth() / 12, initPanel.getHeight() / 2, initPanel.getWidth() / 6 * 4, 30);
				
				exportPathLable.setLocation(initPanel.getWidth() / 12, initPanel.getHeight() / 2 - 20);
				exportPathLable.setSize(500, 20);
				
				thm.setBounds(initPanel.getWidth() / 4 * 3, initPanel.getHeight() / 2, initPanel.getWidth()/6, 30);
			}
		});
		
		gui.add(initPanel);
	}
	
	private void setTheme(Theme theme) {
		this.theme = theme;
	}
	
	private void updateColors() {
		this.background = this.theme.getBg();
		this.text = this.theme.getText();
		this.button = this.theme.getBtn();
		
		cls.setForeground(text);
		cls.setBackground(button);
		
		versionChooserLabel.setForeground(text);
		versionChooserLabel.setBackground(background);
		
		versionChooser.setForeground(text);
		versionChooser.setBackground(background);
		
		exportPathLable.setForeground(text);
		exportPathLable.setBackground(background);
		
		exportPath.setForeground(text);
		exportPath.setBackground(background);
		
		exportJar.setForeground(text);
		exportJar.setBackground(button);
		
		thm.setForeground(text);
		thm.setBackground(button);
		
		initPanel.setBackground(background);
	}
	
	private void onClose() {
		this.cfg.setExportPath(exportPath.getText());
		System.exit(0);
	}
	
	public JFrame getFrame() {
		return this.gui;
	}
}
