package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.data.DS2ConcurrentHashMap;
import com.evanbelcher.DrillSweet2.display.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PagePlayer implements Runnable {

	private DS2DesktopPane desktopPane;
	private HashMap<MovingPoint, String> points = new HashMap<>();
	private GraphicsRunner graphicsRunner;
	private boolean stop;

	public PlayerDesktopPane getPlayerDesktopPane() {
		while (playerDesktopPane == null)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return playerDesktopPane;
	}

	PlayerDesktopPane playerDesktopPane;

	public PagePlayer(DS2ConcurrentHashMap<Point, String> previousMap, DS2ConcurrentHashMap<Point, String> currentMap, int counts, DS2DesktopPane desktopPane, GraphicsRunner graphicsRunner) {
		//setUndecorated(false);
		stop = false;
		JFrame.setDefaultLookAndFeelDecorated(true);
		this.desktopPane = desktopPane;
		int offset = 0;//desktopPane.getGraphicsRunner().getJMenuBar().getHeight();
		for (Point p : previousMap.keySet()) {
			String name = previousMap.get(p);
			for (Point p2 : currentMap.keySet())
				if (currentMap.get(p2).equals(name))
					points.put(new MovingPoint(new Point(p.x, p.y + offset), new Point(p2.x, p2.y + offset), counts * 30), name);
		}
		this.graphicsRunner = graphicsRunner;
	}

	@Override public void run() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		//set up the frame
		PlayerInternalFrame playerInternalFrame = new PlayerInternalFrame(this, graphicsRunner);
		playerDesktopPane = new PlayerDesktopPane(playerInternalFrame, desktopPane, points);

		//thread-safety pauses
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		//normal loop - infinite
		//noinspection InfiniteLoopStatement
		while (!stop) {
			try {
				playerDesktopPane.repaint(); //call paint() method for graphics
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			if (playerInternalFrame.getState().equals("play"))
				for (MovingPoint p : points.keySet()) {
					p.next();
				}
			else if (playerInternalFrame.getState().equals("rewind"))
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

	@SuppressWarnings("SameParameterValue") public void setStop(boolean stop) {
		this.stop = stop;
	}

	public HashMap<MovingPoint, String> getPoints() {
		return points;
	}
}
