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

package com.evanbelcher.DrillSweet2.play.data;

import com.evanbelcher.DrillSweet2.display.DS2DesktopPane;

import java.awt.*;

/**
 * Holds data for the motion of one point to another location
 *
 * @author Evan Belcher
 */
public class MovingPoint {

	private double dx;
	private double dy;
	private Point currentPoint;
	private Point start;
	private Point end;
	private int index;
	private int subdivisions;
	private Color color;

	/**
	 * @param oldPoint     the location the point starts at
	 * @param newPoint     the location the point ends at
	 * @param subdivisions the amount of subdivisions of that total motion
	 */
	public MovingPoint(Point oldPoint, Point newPoint, int subdivisions) {
		dx = (newPoint.getX() - oldPoint.getX()) / (double) subdivisions;
		dy = (newPoint.getY() - oldPoint.getY()) / (double) subdivisions;
		currentPoint = new Point(oldPoint);
		start = new Point(oldPoint);
		end = new Point(newPoint);
		index = 0;
		this.subdivisions = subdivisions;

		int counts = subdivisions / 30;
		if (start.distance(start.getX() + subdivisions * dx, start.getY() + subdivisions * dy) / counts > 2 * (DS2DesktopPane.getField().getWidth() / 100))
			color = Color.RED;
		else
			color = Color.BLACK;
	}

	/**
	 * Moves the point towards the end location by one subdivided amount
	 */
	public void next() {
		if (index < subdivisions) {
			index++;
			updatePoint();
		}
	}

	/**
	 * Moves the point towards the starting location by one subdivided amount
	 */
	public void back() {
		if (index > 0) {
			index--;
			updatePoint();
		}
	}

	/**
	 * Returns the current location of the point
	 */
	public Point current() {
		return currentPoint;
	}

	/**
	 * Sets the location to be accurate
	 */
	private void updatePoint() {
		currentPoint.setLocation(start.getX() + dx * index, start.getY() + dy * index);
	}

	/**
	 * Sends the point to the starting location
	 */
	public void start() {
		currentPoint.setLocation(start);
		index = 0;
	}

	/**
	 * Sends the point to the ending location
	 */
	public void end() {
		currentPoint.setLocation(end);
		index = subdivisions;
	}

	/**
	 * Returns the color of this dot
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color to blue if it will collide with another point
	 */
	public void setCollides() {
		if (color.equals(Color.BLACK))
			color = Color.BLUE;
	}

	@Override public String toString() {
		return "MovingPoint: " + start + " to " + end + " in " + subdivisions + " counts";
	}

	/**
	 * Draws gradient line from start to end
	 *
	 * @param g2d
	 */
	public void drawLine(Graphics2D g2d) {
		GradientPaint gradient = new GradientPaint(start.x, start.y, Color.BLUE, (float) (start.x + subdivisions * dx), (float) (start.y + subdivisions * dy), Color.GREEN);
		g2d.setPaint(gradient);
		g2d.setStroke(new BasicStroke(1));
		g2d.drawLine(start.x, start.y, (int) (start.x + subdivisions * dx), (int) (start.y + subdivisions * dy));
	}

	/**
	 * Returns the starting point
	 */
	public Point getStart() {
		return start;
	}

	/**
	 * Returns the ending point
	 */
	public Point getEnd() {
		return end;
	}
}
