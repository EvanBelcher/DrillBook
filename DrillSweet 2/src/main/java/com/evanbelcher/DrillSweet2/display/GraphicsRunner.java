package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.State;

/**
 * Custom JFrame and Runnable for the application
 * 
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class GraphicsRunner extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 9006087905794888130L;
	public static final Rectangle SCREEN_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	
	private DS2DesktopPane desktop;
	
	/**
	 * Initializes and sets up frame
	 * 
	 * @since 1.0
	 */
	@Override
	public void run() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		try {
			desktop = new DS2DesktopPane();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		setContentPane(desktop);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(SCREEN_SIZE);
		setResizable(false);
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				if (!State.isDebugMode()) {
					int i2 = JOptionPane.showConfirmDialog(null, "Would you like to save your work first?", "Unsaved Work", JOptionPane.YES_NO_CANCEL_OPTION);
					if (i2 == 2) {
						setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						return;
					}
					if (i2 == 0) {
						Main.save();
					}
				}
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
		});
		setJMenuBar(new DS2MenuBar(this, desktop));
		try {
			setIconImage(ImageIO.read(Main.getFile("icon.png")));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		setVisible(true);
		
		//thread-safety pause
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//game loop - infinite
		while (true) {
			try {
				desktop.repaint(); //call paint() method for graphics
			} catch (NullPointerException e) {}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Sets the name and title of the JFrame
	 * 
	 * @param str
	 *            the name to use
	 * @since 1.0
	 */
	public void setWindowTitle(String str) {
		setName(str);
		setTitle(str);
	}
	
}