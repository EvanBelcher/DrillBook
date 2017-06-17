/**
 * Drill Sweet 2 is a marching band drill creation software.
 * Copyright (C) 2017  Evan Belcher
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.evanbelcher.DrillBook.display.data;

import java.awt.*;
import java.util.Vector;

public class Selection extends Vector<Point> {

	private Point anchor;

	public Selection(Vector<Point> activePoints) {
		super(activePoints);
		makeRelativePoints();
	}

	@Override public boolean add(Point p) {
		makeAbsolutePoints();
		boolean b = super.add(p);
		makeRelativePoints();
		return b;
	}

	@Override public boolean remove(Object o) {
		makeAbsolutePoints();
		boolean b = super.remove(o);
		makeRelativePoints();
		return b;
	}

	@Override public Point get(int index) {
		makeAbsolutePoints();
		Point p = super.get(index);
		makeRelativePoints();
		return p;
	}

	@Override public boolean contains(Object o) {
		makeAbsolutePoints();
		boolean b = super.contains(o);
		makeRelativePoints();
		return b;
	}

	private void makeRelativePoints() {
		anchor = new Point();
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Point p : this) {
			if (p != null) {
				minX = Math.min(minX, p.x);
				minY = Math.min(minY, p.y);
			}
		}
		anchor.setLocation(minX, minY);
		for (Point p : this)
			if (p != null)
				p.setLocation(p.x - minX, p.y - minY);

	}

	private void makeAbsolutePoints() {
		for (Point p : this)
			if (p != null)
				p.setLocation(p.x + anchor.x, p.y + anchor.y);
	}

	public Vector<Point> getAbsolutePoints(Point anchor) {
		Vector<Point> points = new Vector<>(this);
		for (Point p : points)
			p.setLocation(p.x + anchor.x, p.y + anchor.y);
		return points;
	}

	public Point getAnchor() {
		return anchor;
	}
}
