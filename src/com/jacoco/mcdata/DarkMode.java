package com.jacoco.mcdata;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jacoco.mcdata.files.Config;

public class DarkMode {

	public static int br;
	public static int bg;
	public static int bb;
	
	public static int tr;
	public static int tg;
	public static int tb;
	
	public static void setValues(int br, int bg, int bb, int tr, int tg, int tb, String mode) throws IOException {
		new Gui(br, bg, bb, tr, tg, tb, mode);
	}
	
	public static void error() {
		
		Dialog d = new Dialog(Gui.gui);
		d.setLayout(null);

		// coordinates
		int x = 500;
		int y = 200;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = screenSize.width/2;
		int centerY = screenSize.height/3;
		
		// labels
		Font title = new Font("TimesRoman", Font.BOLD, 30);
		Label head = new Label("Restart Required");
		head.setFont(title);
		head.setSize(500, 50);
		head.setLocation((x/2-100), 50);
		d.add(head);
		
		Font p = new Font("TimesRoman", Font.PLAIN, 15);
		Label detail = new Label("If you want to update the theme, you need to restart the program");
		detail.setFont(p);
		detail.setSize(500, 20);
		detail.setLocation(x/2-175, 100);
		d.add(detail);
		
		// ok button
		Button ok = new Button("Ok");
		ok.setBounds(50, 30, 100, 30);
	    ok.setLocation((x/2-50)+10, 130);
	    d.add(ok);
	    ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
			}
		});
		
	    // make dialog work
	    d.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				d.setVisible(false);
			}
		});
	    
		d.setBounds(centerX-x/2, centerY, x, y);
		d.setVisible(true);
	}
	
	@SuppressWarnings("unchecked")
	public static void setMode(String color) {
		FileReader reader = null;
		try {
			reader = new FileReader(Config.cfg);
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		}
	    JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(reader);
		} catch (IOException | ParseException e2) {
			e2.printStackTrace();
		}
		
	    jsonObject.put("mode", color);
	    
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(Config.cfg);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
        pw.write(jsonObject.toJSONString()); 
        
        pw.flush(); 
        pw.close(); 
	}
}