package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class PlayerMenuBar extends JMenuBar implements ActionListener {

	PagePlayer pagePlayer;
	PlayerPanel playerPanel;
	private String state;

	public PlayerMenuBar(PagePlayer pagePlayer, PlayerPanel playerPanel) {
		super();
		this.pagePlayer = pagePlayer;
		this.playerPanel = playerPanel;
		state = "pause";

		JMenuItem menuItem = new JMenuItem(getIcon("start"));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("start");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem(getIcon("rewind"));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("rewind");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem(getIcon("play"));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("play");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem(getIcon("pause"));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("pause");
		menuItem.addActionListener(this);
		add(menuItem);

		add(menuItem);
		menuItem = new JMenuItem(getIcon("end"));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("end");
		menuItem.addActionListener(this);
		add(menuItem);
	}

	/**
	 * Invoked when an action occurs.
	 *
	 * @param e
	 */
	@Override public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "start":
				state = "pause";
				for (MovingPoint p : pagePlayer.getPoints().keySet())
					p.reset();
				break;
			case "rewind":
				state = "rewind";
				break;
			case "play":
				state = "play";
				break;
			case "pause":
				state = "pause";
				break;
			case "end":
				state = "pause";
				for (MovingPoint p : pagePlayer.getPoints().keySet())
					p.end();
				break;
		}
	}

	public String getState() {
		return state;
	}

	public ImageIcon getIcon(String s) {
		try {
			return new ImageIcon(ImageIO.read(Main.getFile(s + ".png", this)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
