package com.evanbelcher.DrillSweet2.data;

import com.evanbelcher.DrillSweet2.Main;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

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

	public ConcurrentHashMap<Integer, Page> getPages() {
		return pages;
	}
}
