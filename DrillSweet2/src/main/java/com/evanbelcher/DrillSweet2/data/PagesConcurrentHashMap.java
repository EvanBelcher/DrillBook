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

package com.evanbelcher.DrillSweet2.data;

import com.evanbelcher.DrillSweet2.Main;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the pages as well as the field in order to scale properly among different screen sizes
 *
 * @author Evan Belcher
 */
public class PagesConcurrentHashMap {

	private ConcurrentHashMap<Integer, Page> pages;
	private Rectangle field;

	public PagesConcurrentHashMap() {
		pages = new ConcurrentHashMap<>();
		field = new Rectangle();
	}

	/**
	 * Sets the field to the given field
	 *
	 * @param field
	 */
	public void setField(Rectangle field) {
		this.field = field;
	}

	/**
	 * Adjusts all points to be scaled and translated properly to the new field size. Also sets the field to the given field (uses setField()).
	 *
	 * @param newField the new field rectangle
	 */
	public void fixPoints(Rectangle newField) {
		if (field == null)
			field = newField;
		if (!newField.equals(field)) {
			double xScale = newField.getWidth() / field.getWidth();
			double yScale = newField.getHeight() / field.getHeight();

			for (Page page : Main.getPages().values()) {
				PointConcurrentHashMap<Point, String> dots = page.getDots();
				PointConcurrentHashMap<Point, String> newDots = new PointConcurrentHashMap<>();
				for (Point point : dots.keySet()) {
					Point newPoint = new Point((int) Math.round((point.getX() - field.getX()) * xScale + newField.getX()), (int) Math.round((point.getY() - field.getY()) * yScale + newField.getY()));
					newDots.put(newPoint, dots.get(point));
				}
				page.setDots(newDots);
			}
			setField(newField);
		}
	}

	/**
	 * Returns the pages map
	 */
	public ConcurrentHashMap<Integer, Page> getPages() {
		return pages;
	}
}
