package com.evanbelcher.DrillSweet2.data;

public class Settings {

	private boolean showGrid;
	private boolean showNames;
	private boolean colorDots;
	private boolean collegeHashes;
	private int fontSize;

	public Settings() {
		showGrid = true;
		showNames = true;
		colorDots = false;
		collegeHashes = false;
		fontSize = 12;
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

	public boolean isColorDots() {
		return colorDots;
	}

	public void setColorDots(boolean colorDots) {
		this.colorDots = colorDots;
	}

	public boolean isCollegeHashes() {
		return collegeHashes;
	}

	public void setCollegeHashes(boolean collegeHashes) {
		this.collegeHashes = collegeHashes;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
