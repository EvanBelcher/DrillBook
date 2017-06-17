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

package com.evanbelcher.DrillBook.play;

import com.evanbelcher.DrillBook.data.*;
import com.evanbelcher.DrillBook.display.*;
import com.evanbelcher.DrillBook.play.data.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * The driver class for the player
 *
 * @author Evan Belcher
 */
public class PagePlayer implements Runnable {

	private DBDesktopPane desktopPane;
	private HashMap<MovingPoint, String> points = new HashMap<>();
	private GraphicsRunner graphicsRunner;
	private PlayerDesktopPane playerDesktopPane;
	private boolean stop;

	/**
	 * Sets up the fields and makes the points object
	 *
	 * @param previousMap    the previous dot map
	 * @param currentMap     the current dot map
	 * @param counts         the number of counts to make the move
	 * @param desktopPane    the DBDesktopPane
	 * @param graphicsRunner the GraphicsRunner
	 */
	public PagePlayer(PointConcurrentHashMap<Point, String> previousMap, PointConcurrentHashMap<Point, String> currentMap, int counts, DBDesktopPane desktopPane, GraphicsRunner graphicsRunner) {
		stop = false;
		this.desktopPane = desktopPane;
		for (Point p : previousMap.keySet()) {
			String name = previousMap.get(p);
			for (Point p2 : currentMap.keySet())
				if (currentMap.get(p2).equals(name))
					points.put(new MovingPoint(new Point(p.x, p.y), new Point(p2.x, p2.y), counts * 30), name);
		}

		ArrayList<MovingPoint> mpList = new ArrayList<>();
		mpList.addAll(points.keySet());
		//		for (int i = 0; i < mpList.size(); i++)
		//			System.out.println(i + " " + mpList.get(i));

		for (int i = 0; i < mpList.size() - 1; i++)
			for (int j = i + 1; j < mpList.size(); j++) {
				State.print(points.get(mpList.get(i)) + " " + points.get(mpList.get(j)));
				if (CollisionDetector.findCollision(mpList.get(i), mpList.get(j))) {
					mpList.get(i).setCollides();
					mpList.get(j).setCollides();
				}
			}
		this.graphicsRunner = graphicsRunner;
	}

	/**
	 * Sets up the playerInternalFrame and sets up and repaints the playerDesktopPane
	 */
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
		while (!stop) {
			try {
				playerDesktopPane.repaint(); //call paint() method for graphics
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			if (playerInternalFrame.getState().equals("data"))
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

	/**
	 * Sets the value of stop. 'true' stops the thread
	 *
	 * @param stop new value of stop
	 */
	@SuppressWarnings("SameParameterValue") public void setStop(boolean stop) {
		this.stop = stop;
	}

	/**
	 * Returns points
	 */
	public HashMap<MovingPoint, String> getPoints() {
		return points;
	}

	/**
	 * Returns the PlayerDesktopPane
	 */
	public PlayerDesktopPane getPlayerDesktopPane() {
		while (playerDesktopPane == null)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return playerDesktopPane;
	}
}
