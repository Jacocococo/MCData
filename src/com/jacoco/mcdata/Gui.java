package com.jacoco.mcdata;

import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Gui extends Frame {
	
	public Gui() {
		
		Frame gui = new Frame(Strings.name);
		
		// add "close" button
		Button cls = new Button(Strings.close);
		cls.setBounds(30, 30, 100, 30);
		gui.add(cls);
		cls.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		
		// file explorer
	    TextField fe = new TextField(Strings.file);  
	    fe.setBounds(50, 100, 500, 30);  
	    fe.setLocation(30, 75);
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
                fe.setText(fd.getDirectory().substring(0, fel-1));
                new com.jacoco.mcdata.fileexplorer.MapLocation(fe.getText());
                
			}
		});
	    
	    // make application work
		gui.addWindowListener(new WindowAdapter(){  
	           public void windowClosing(WindowEvent e) {  
	               System.exit(0);
	           }  
	    }); 

		gui.setSize(1200, 900);
		gui.setLayout(null);
		gui.setVisible(true);
	}
	
}