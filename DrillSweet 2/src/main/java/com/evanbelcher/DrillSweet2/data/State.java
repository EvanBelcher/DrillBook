package com.evanbelcher.DrillSweet2.data;

import com.evanbelcher.DrillSweet2.Main;

import java.awt.*;
import java.util.ArrayDeque;

/**
 * Holds information about the current state of the application
 *
 * @author Evan Belcher
 */
@SuppressWarnings("unused") public class State {

	private static final boolean DEBUG_MODE = false;
	public static final String VERSION = "v1.3.0";

	private int currentPage;
	private boolean showGrid = true;
	private boolean showNames = true;
	private String currentFileName;
	private String filePath;
	private Rectangle field;

	private transient ArrayDeque<DS2ConcurrentHashMap<Point, String>> history;
	private transient ArrayDeque<DS2ConcurrentHashMap<Point, String>> future;

	/**
	 * Constructs the object with given current page.
	 *
	 * @param currentPage the current page number
	 */
	public State(int currentPage, String filePath, String currentFileName) {
		this.currentPage = currentPage;
		this.filePath = filePath;
		this.currentFileName = currentFileName;
		history = new ArrayDeque<>();
		future = new ArrayDeque<>();
	}

	/**
	 * Returns if the program is in debug mode
	 */
	public static boolean isDebugMode() {
		return DEBUG_MODE;
	}

	/**
	 * Returns current page number
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Sets current page number
	 *
	 * @param currentPage the current page number
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * Returns show grid?
	 */
	public boolean isShowGrid() {
		return showGrid;
	}

	/**
	 * Sets showGrid
	 *
	 * @param showGrid whether the grid should be shown
	 */
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	/**
	 * Returns show names?
	 */
	public boolean isShowNames() {
		return showNames;
	}

	/**
	 * Sets showNames
	 *
	 * @param showNames whether the names should be shown
	 */
	public void setShowNames(boolean showNames) {
		this.showNames = showNames;
	}

	/**
	 * Returns currentFileName
	 */
	public String getCurrentFileName() {
		return currentFileName;
	}

	/**
	 * Sets currentFileName
	 *
	 * @param currentFileName the new current file name
	 */
	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

	/**
	 * Returns the file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the file path
	 *
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
				DS2ConcurrentHashMap<Point, String> dots = page.getDots();
				DS2ConcurrentHashMap<Point, String> newDots = new DS2ConcurrentHashMap<>();
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
	 * Default toString()
	 */
	@Override public String toString() {
		return "State{" + "currentPage=" + currentPage + ", showGrid=" + showGrid + ", showNames=" + showNames + ", currentFileName='" + currentFileName + '\'' + ", filePath='" + filePath + '\'' + '}';
	}

	/**
	 * Prints objects to console if debug mode is true
	 *
	 * @param objects the object or objects to be printed (varargs)
	 */
	public static void print(Object... objects) {
		if (DEBUG_MODE) {
			for (Object o : objects)
				System.out.println(o);
		}
	}

	/**
	 * Adds current page to history and makes sure the history is a maximum of 20 pages.
	 */
	public void addHistory() {
		checkVars();
		history.offer(new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots()));
		while (history.size() > 20)
			history.pollFirst();
		future.clear();
	}

	/**
	 * Adds current page to future and makes sure the future is a maximum of 20 pages.
	 */
	public void addFuture() {
		checkVars();
		future.offer(new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots()));
		while (future.size() > 20)
			future.pollFirst();
	}

	/**
	 * Reverts the current page to the most recent page in history
	 */
	public void undo() {
		checkVars();
		if (history.size() > 0) {
			DS2ConcurrentHashMap<Point, String> current;
			do {
				current = new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots());
				future.offer(history.pollLast());
				while (future.size() > 20)
					future.pollFirst();
				Main.getCurrentPage().setDots(future.peekLast());
			} while (history.size() > 0 && Main.getCurrentPage().getDots().equals(current));
		}
	}

	/**
	 * Reverts the current page to the most recent page in future
	 */
	public void redo() {
		checkVars();
		if (future.size() > 0) {
			DS2ConcurrentHashMap<Point, String> current;
			do {
				current = new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots());
				history.offer(future.pollLast());
				while (history.size() > 20)
					history.pollFirst();
				Main.getCurrentPage().setDots(history.peekLast());
			} while (future.size() > 0 && Main.getCurrentPage().getDots().equals(current));
		}
	}

	/**
	 * Makes sure history and future are not null;
	 */

	private void checkVars() {
		if (history == null)
			history = new ArrayDeque<>();
		if (future == null)
			future = new ArrayDeque<>();
	}

}
