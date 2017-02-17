package com.evanbelcher.DrillSweet2.data;

/**
 * The settings object for non-essential user-defined settings
 *
 * @author Evan Belcher
 */
public class Settings {

	private boolean showGrid;
	private boolean showNames;
	private boolean showText;
	private boolean colorDots;
	private boolean collegeHashes;
	private int fontSize;

	public Settings() {
		showGrid = true;
		showNames = true;
		showText = true;
		colorDots = false;
		collegeHashes = false;
		fontSize = 12;
	}

	/**
	 * Returns whether the grid should be shown
	 */
	public boolean shouldShowGrid() {
		return showGrid;
	}

	/**
	 * Sets whether the grid should be shown to the given value
	 *
	 * @param showGrid whether the grid should be shown
	 */
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	/**
	 * Returns whether we should show dot names
	 */
	public boolean shouldShowNames() {
		return showNames;
	}

	/**
	 * Sets whether we whould show dot names to the given value
	 *
	 * @param showNames whether the names should be shown
	 */
	public void setShowNames(boolean showNames) {
		this.showNames = showNames;
	}

	/**
	 * Returns whether we should show the text box
	 */
	public boolean shouldShowText() {
		return showText;
	}

	/**
	 * Sets whether we should show the text box
	 *
	 * @param showText
	 */
	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	/**
	 * Returns whether the dots should be color coded
	 */
	public boolean shouldColorDots() {
		return colorDots;
	}

	/**
	 * Sets whether the dots should be color coded to the given value
	 *
	 * @param colorDots
	 */
	public void setColorDots(boolean colorDots) {
		this.colorDots = colorDots;
	}

	/**
	 * Returns whether we should use college hashes (4 steps inside high school hashes)
	 */
	public boolean useCollegeHashes() {
		return collegeHashes;
	}

	/**
	 * Sets whether we should use college hashes (4 steps inside high school hashes)
	 *
	 * @param collegeHashes
	 */
	public void setCollegeHashes(boolean collegeHashes) {
		this.collegeHashes = collegeHashes;
	}

	/**
	 * Returns the font size
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * Sets the font size to the given value
	 *
	 * @param fontSize
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

}

class Notifications {

	private boolean openingInstructions;
}