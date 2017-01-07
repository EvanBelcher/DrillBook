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
	public static final String VERSION = "v1.4.0";

	private String filePath;
	private String currentFileName;
	private int currentPage;


	private Settings settings;

	private transient ArrayDeque<PointConcurrentHashMap<Point, String>> history;
	private transient ArrayDeque<PointConcurrentHashMap<Point, String>> future;

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
		settings = new Settings();
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
	 * Returns the settings object
	 */
	public Settings getSettings() {
		if (settings == null)
			settings = new Settings();
		return settings;
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
		return "State{" + "currentPage=" + currentPage + ", currentFileName='" + currentFileName + '\'' + ", filePath='" + filePath + '\'' + ", settings=" + settings + ", history=" + history + ", future=" + future + '}';
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
		history.offer(new PointConcurrentHashMap<>(Main.getCurrentPage().getDots()));
		while (history.size() > 20)
			history.pollFirst();
		future.clear();
	}

	/**
	 * Adds current page to future and makes sure the future is a maximum of 20 pages.
	 */
	public void addFuture() {
		checkVars();
		future.offer(new PointConcurrentHashMap<>(Main.getCurrentPage().getDots()));
		while (future.size() > 20)
			future.pollFirst();
	}

	/**
	 * Reverts the current page to the most recent page in history
	 */
	public void undo() {
		checkVars();
		if (history.size() > 0) {
			PointConcurrentHashMap<Point, String> current;
			do {
				current = new PointConcurrentHashMap<>(Main.getCurrentPage().getDots());
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
			PointConcurrentHashMap<Point, String> current;
			do {
				current = new PointConcurrentHashMap<>(Main.getCurrentPage().getDots());
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
