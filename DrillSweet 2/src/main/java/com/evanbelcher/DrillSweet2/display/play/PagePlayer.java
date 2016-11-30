package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.data.DS2ConcurrentHashMap;
import com.evanbelcher.DrillSweet2.display.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class PagePlayer extends JFrame implements Runnable {

	private DS2DesktopPane desktopPane;
	private HashMap<MovingPoint, String> points = new HashMap<>();

	public PagePlayer(DS2ConcurrentHashMap<Point, String> previousMap, DS2ConcurrentHashMap<Point, String> currentMap, int counts, DS2DesktopPane desktopPane) {
		super();
		this.desktopPane = desktopPane;
		for (Point p : previousMap.keySet()) {
			String name = previousMap.get(p);
			for (Point p2 : currentMap.keySet())
				if (currentMap.get(p2).equals(name))
					points.put(new MovingPoint(p, p2, counts * 30), name);
		}
	}

	@Override public void run() {
		//set up the frame
		PlayerPanel playerPanel = new PlayerPanel(desktopPane, points);
		setContentPane(playerPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(GraphicsRunner.SCREEN_SIZE);
		setResizable(false);
		PlayerMenuBar playerMenuBar = new PlayerMenuBar(this, playerPanel);
		setJMenuBar(playerMenuBar);

		try {
			setIconImage(ImageIO.read(Main.getFile("playicon.png", this)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setWindowTitle();
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
				playerPanel.repaint(); //call paint() method for graphics
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			if (playerMenuBar.getState().equals("play"))
				for (MovingPoint p : points.keySet()) {
					p.next();
				}
			else if (playerMenuBar.getState().equals("rewind"))
				for (MovingPoint p : points.keySet()) {
					p.back();
				}
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the name and title of the JFrame
	 */
	public void setWindowTitle() {
		setName("Player");
		setTitle("Player");
	}

	public HashMap<MovingPoint, String> getPoints() {
		return points;
	}
}
