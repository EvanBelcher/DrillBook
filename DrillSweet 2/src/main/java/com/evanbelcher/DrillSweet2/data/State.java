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

	private static final boolean DEBUG_MODE = true;
	private int currentPage;
	private boolean showGrid = true;
	private boolean showNames = true;
	private String currentFileName;
	private String filePath;
	private transient ArrayDeque<DS2ConcurrentHashMap<Point, String>> history;
	private transient ArrayDeque<DS2ConcurrentHashMap<Point, String>> future;
	private transient boolean justUndid = false; //TODO really should fix
	private transient boolean justRedid = false;

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
		justUndid = false;
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

	public void addHistory() {
		checkVars();
		history.offer(new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots()));
		while (history.size() > 20)
			history.pollFirst();
		clearFuture();
	}

	public void addPresent() {
		checkVars();
		future.offer(new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots()));
		while (future.size() > 20)
			future.pollFirst();
	}

	public void undo() {
		checkVars();
		if (history.size() > 0) {
			future.offer(history.pollLast());
			while (future.size() > 20)
				future.pollFirst();
			Main.getCurrentPage().setDots(future.peekLast());
			if (justRedid) {
				justRedid = false;
				undo();
			}
			justUndid = true;
		}
	}

	public void redo() {
		checkVars();
		if (future.size() > 0) {
			history.offer(future.pollLast());
			while (history.size() > 20)
				history.pollFirst();
			Main.getCurrentPage().setDots(history.peekLast());
			if (justUndid) {
				justUndid = false;
				redo();
			}
			justRedid = true;
		}
	}

	private void checkVars() {
		if (history == null)
			history = new ArrayDeque<>();
		if (future == null)
			future = new ArrayDeque<>();
	}

	private void clearFuture() {
		future.clear();
	}

}
