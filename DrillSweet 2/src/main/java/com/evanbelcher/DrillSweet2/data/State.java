package com.evanbelcher.DrillSweet2.data;

/**
 * Holds information about the current state of the application
 *
 * @author Evan Belcher
 */
@SuppressWarnings("unused") public class State {

	private static final boolean DEBUG_MODE = false;
	private int currentPage;
	private boolean showGrid = true;
	private boolean showNames = true;
	private String currentFileName;
	private String filePath;

	/**
	 * Constructs the object with given current page.
	 *
	 * @param currentPage the current page number
	 */
	public State(int currentPage, String filePath, String currentFileName) {
		this.currentPage = currentPage;
		this.filePath = filePath;
		this.currentFileName = currentFileName;
	}

	/**
	 * Returns if the program is in debug mode
	 */
	public static boolean isDebugMode() {
		return DEBUG_MODE;
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

}
