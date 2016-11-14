
package com.evanbelcher.DrillSweet2.data;

import java.awt.Point;
import java.text.DecimalFormat;

/**
 * Object to hold data about an individual page
 * 
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class Page {
	
	private int number;
	private String song;
	private int startingMeasure = Integer.MIN_VALUE;
	private int endingMeasure = Integer.MAX_VALUE;
	private int counts;
	private String notes;
	private Point textPoint;
	private DS2ConcurrentHashMap<Point, String> dots;
	
	/**
	 * Constructs the page object with the given number. All other fields are default.
	 * 
	 * @param number
	 *            the page number
	 * @since 1.0
	 */
	public Page(int number) {
		setNumber(number);
		setSong("");
		setStartingMeasure(1);
		setEndingMeasure(1);
		setCounts(1);
		setNotes("");
		setTextPoint(new Point(0, 0));
		dots = new DS2ConcurrentHashMap<>();
	}
	
	/**
	 * Constructs the page object with the given number, song, starting measure, and dots. Uses the
	 * starting measure as both the starting and ending measures. All other fields are default.
	 * 
	 * @param number
	 *            the page number
	 * @param song
	 *            the song title
	 * @param starting
	 *            measure the starting measure number
	 * @param dots
	 *            the map of dots
	 * @since 1.0
	 */
	public Page(int number, String song, int startingMeasure, DS2ConcurrentHashMap<Point, String> dots) {
		setNumber(number);
		setSong(song);
		setStartingMeasure(startingMeasure);
		setEndingMeasure(startingMeasure);
		setCounts(1);
		setNotes("");
		setTextPoint(new Point(0, 0));
		this.dots = new DS2ConcurrentHashMap<Point, String>(dots);
	}
	
	/**
	 * Constructs the page object with no default values.
	 * 
	 * @param number
	 *            the page number
	 * @param song
	 *            the song title
	 * @param startingMeasure
	 *            the starting measure
	 * @param endingMeasure
	 *            the ending measure
	 * @param counts
	 *            the counts
	 * @param notes
	 *            the notes
	 * @param textPoint
	 *            the top-left corner for the page text to be drawn at
	 * @param dots
	 *            the map of dots
	 * @since 1.0
	 */
	public Page(int number, String song, int startingMeasure, int endingMeasure, int counts, String notes, Point textPoint, DS2ConcurrentHashMap<Point, String> dots) {
		setNumber(number);
		setSong(song);
		setStartingMeasure(startingMeasure);
		setEndingMeasure(endingMeasure);
		setCounts(counts);
		setNotes(notes);
		setTextPoint(textPoint);
		this.dots = new DS2ConcurrentHashMap<Point, String>(dots);
	}
	
	/**
	 * Constructs the page object using all the values of another page object
	 * 
	 * @param p
	 *            the other Page
	 * @since 1.0
	 */
	public Page(Page p) {
		setNumber(p.getNumber());
		setSong(p.getSong());
		setStartingMeasure(p.getStartingMeasure());
		setEndingMeasure(p.getEndingMeasure());
		setCounts(p.getCounts());
		setNotes(p.getNotes());
		setTextPoint(p.getTextPoint());
		this.dots = p.getDots();
	}
	
	/**
	 * @return the page number
	 * @since 1.0
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Sets the page number
	 * 
	 * @param number
	 * @since 1.0
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	/**
	 * @return the song title
	 * @since 1.0
	 */
	public String getSong() {
		return song;
	}
	
	/**
	 * Sets the song title
	 * 
	 * @param song
	 * @since 1.0
	 */
	public void setSong(String song) {
		this.song = song;
	}
	
	/**
	 * @return the starting measure number
	 * @since 1.0
	 */
	public int getStartingMeasure() {
		return startingMeasure;
	}
	
	/**
	 * Sets the starting measure number
	 * 
	 * @param startingMeasure
	 * @since 1.0
	 */
	public void setStartingMeasure(int startingMeasure) {
		this.startingMeasure = startingMeasure;
	}
	
	/**
	 * @return the ending measure number
	 * @since 1.0
	 */
	public int getEndingMeasure() {
		return endingMeasure;
	}
	
	/**
	 * Sets the ending measure number
	 * 
	 * @param endingMeasure
	 * @since 1.0
	 */
	public void setEndingMeasure(int endingMeasure) {
		this.endingMeasure = endingMeasure;
	}
	
	/**
	 * @return the counts
	 * @since 1.0
	 */
	public int getCounts() {
		return counts;
	}
	
	/**
	 * Sets the counts
	 * 
	 * @param counts
	 * @since 1.0
	 */
	public void setCounts(int counts) {
		this.counts = counts;
	}
	
	/**
	 * @return the notes
	 * @since 1.0
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * Sets the notes
	 * 
	 * @param notes
	 * @since 1.0
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * @return the the top-left corner for the page text to be drawn at
	 * @since 1.0
	 */
	public Point getTextPoint() {
		return textPoint;
	}
	
	/**
	 * Sets the top-left corner for the page text to be drawn at
	 * 
	 * @param textPoint
	 * @since 1.0
	 */
	public void setTextPoint(Point textPoint) {
		this.textPoint = textPoint;
	}
	
	/**
	 * @return the map of dots
	 * @since 1.0
	 */
	public DS2ConcurrentHashMap<Point, String> getDots() {
		return dots;
	}
	
	/**
	 * The well-fomatted representation of this object
	 * 
	 * @return the display string
	 * @since 1.0
	 */
	public String toDisplayString() {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(number) + " | " + (song.isEmpty() ? "SongTitle" : song) + ", m." + startingMeasure + "-" + endingMeasure;
	}
	
	/**
	 * Default toString
	 * 
	 * @return String representation of this object
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return "Page [number=" + number + ", song=" + song + ", startingMeasure=" + startingMeasure + ", endingMeasure=" + endingMeasure + ", counts=" + counts + ", notes=" + notes + ", dots=" + dots + "]";
	}
	
}
