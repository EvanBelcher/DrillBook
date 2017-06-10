/*
		Drill Sweet 2 is a marching band drill creation software.
		Copyright (C) 2017  Evan Belcher

		This program is free software: you can redistribute it and/or modify
		it under the terms of the GNU General Public License as published by
		the Free Software Foundation, either version 3 of the License, or
		(at your option) any later version.

		This program is distributed in the hope that it will be useful,
		but WITHOUT ANY WARRANTY; without even the implied warranty of
		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
		GNU General Public License for more details.

		You should have received a copy of the GNU General Public License
		along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.evanbelcher.DrillSweet2.display;

import com.evanbelcher.DrillSweet2.*;
import com.evanbelcher.DrillSweet2.data.State;
import com.evanbelcher.DrillSweet2.play.PagePlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Custom JFrame and Runnable for the application
 *
 * @author Evan Belcher
 */
public class GraphicsRunner extends JFrame implements Runnable {

	public static final Rectangle SCREEN_SIZE = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	private static final long serialVersionUID = 9006087905794888130L;
	private DS2MenuBar menuBar;
	private DS2DesktopPane desktop;
	private PagePlayer pagePlayer;

	/**
	 * Initializes and sets up frame
	 */
	@Override public void run() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		//set up the frame
		desktop = new DS2DesktopPane();
		setContentPane(desktop);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(SCREEN_SIZE);
		setResizable(false);
		handleClosing();
		menuBar = new DS2MenuBar(this, desktop);
		setJMenuBar(menuBar);

		try {
			setIconImage(ImageIO.read(Main.getFile("icon.png", this)));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		setVisible(true);

		//thread-safety pauses
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		//normal loop - infinite
		//noinspection InfiniteLoopStatement
		while (true) {
			try {
				desktop.repaint(); //call paint() method for graphics
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Makes a PlayerDesktopPane the contentPane
	 *
	 * @param desktop the current DS2DesktopPane
	 */
	public void toPlayMode(DS2DesktopPane desktop) {
		pagePlayer = new PagePlayer(Main.getPages().get(Main.getState().getCurrentPage() - 1).getDots(), Main.getCurrentPage().getDots(), Main.getCurrentPage().getCounts(), desktop, this);
		new Thread(pagePlayer).start();
		menuBar.disableMost();
		setContentPane(pagePlayer.getPlayerDesktopPane());
	}

	/**
	 * Makes the DS2DesktopPane the contentPane
	 */
	public void toNormalMode() {
		if (pagePlayer != null)
			pagePlayer.setStop(true);
		menuBar.enableAll();
		setContentPane(desktop);
	}

	/**
	 * Sets the name and title of the JFrame
	 *
	 * @param str the name to use
	 */
	public void setWindowTitle(String str) {
		setName(str);
		setTitle(str);
	}

	/**
	 * Ask us if we want to save our work on close.
	 */
	private void handleClosing() {
		addWindowListener(new WindowListener() {

			@Override public void windowActivated(WindowEvent arg0) {
			}

			@Override public void windowClosed(WindowEvent e) {
			}

			/**
			 * Asks to save. If the person says cancel, cancel the close.
			 */
			@Override public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				if (!State.isDebugMode()) {
					int i2 = JOptionPane.showConfirmDialog(null, "Would you like to save your work first?", "Unsaved Work", JOptionPane.YES_NO_CANCEL_OPTION);
					if (i2 == 2) {
						setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						return;
					}
					if (i2 == 0) {
						Main.save();
					}
					while (DotSheetMaker.isPrinting())
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
				}
				try {
					Main.saveState().join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			@Override public void windowDeactivated(WindowEvent e) {
			}

			@Override public void windowDeiconified(WindowEvent e) {
			}

			@Override public void windowIconified(WindowEvent e) {
			}

			@Override public void windowOpened(WindowEvent e) {
			}
		});
	}
}