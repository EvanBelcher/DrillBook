package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.display.DS2DesktopPane;

import java.awt.*;

public class MovingPoint {

	private double dx;
	private double dy;
	private Point currentPoint;
	private Point origin;
	private int index;
	private int subdivisions;

	public MovingPoint(Point oldPoint, Point newPoint, int subdivisions) {
		dx = (newPoint.getX() - oldPoint.getX()) / (double) subdivisions;
		dy = (newPoint.getY() - oldPoint.getY()) / (double) subdivisions;
		currentPoint = new Point(oldPoint);
		origin = new Point(oldPoint);
		index = 0;
		this.subdivisions = subdivisions;
	}

	public Point next() {
		if (index < subdivisions) {
			index++;
			updatePoint();
			return currentPoint;
		}
		return null;
	}

	@SuppressWarnings("unused") public Point back() {
		if (index > 0) {
			index--;
			updatePoint();
			return currentPoint;
		}
		return null;
	}

	public Point current() {
		return currentPoint;
	}

	private void updatePoint() {
		currentPoint.setLocation(origin.getX() + dx * index, origin.getY() + dy * index);
	}

	public void reset() {
		currentPoint.setLocation(origin);
		index = 0;
	}

	public void end() {
		currentPoint.setLocation(origin.getX() + subdivisions * dx, origin.getY() + subdivisions * dy);
		index = subdivisions;
	}

	public Color getColor() {
		int counts = subdivisions / 30;
		if (origin.distance(origin.getX() + subdivisions * dx, origin.getY() + subdivisions * dy) / counts > 2 * (DS2DesktopPane.getField().getWidth() / 100))
			return Color.RED;
		return Color.BLACK;
	}

	@Override public String toString() {
		return "MovingPoint: " + (dx * subdivisions) + "x + " + (dy * subdivisions) + "y in " + subdivisions + " counts";
	}
}
