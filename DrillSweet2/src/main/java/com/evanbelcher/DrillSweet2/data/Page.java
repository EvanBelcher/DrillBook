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

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Object to hold data about an individual page
 *
 * @author Evan Belcher
 */
@SuppressWarnings("unused") public class Page {

	private int number;
	private String song;
	private int startingMeasure = Integer.MIN_VALUE;
	private int endingMeasure = Integer.MAX_VALUE;
	private int counts;
	private String notes;
	private Point textPoint;
	private PointConcurrentHashMap<Point, String> dots;

	/**
	 * Constructs the page object with the given number. All other fields are default.
	 *
	 * @param number the page number
	 */
	public Page(int number) {
		setNumber(number);
		setSong("");
		setStartingMeasure(1);
		setEndingMeasure(1);
		setCounts(1);
		setNotes("");
		setTextPoint(new Point(0, 0));
		dots = new PointConcurrentHashMap<>();
	}

	/**
	 * Constructs the page object with the given number, song, starting measure, and dots. Uses the
	 * starting measure as both the starting and ending measures. All other fields are default.
	 *
	 * @param number          the page number
	 * @param song            the song title
	 * @param startingMeasure he starting measure number
	 * @param dots            the map of dots
	 */
	public Page(int number, String song, int startingMeasure, PointConcurrentHashMap<Point, String> dots) {
		setNumber(number);
		setSong(song);
		setStartingMeasure(startingMeasure);
		setEndingMeasure(startingMeasure);
		setCounts(1);
		setNotes("");
		setTextPoint(new Point(0, 0));
		this.dots = new PointConcurrentHashMap<>(dots);

	}

	/**
	 * Constructs the page object with no default values.
	 *
	 * @param number          the page number
	 * @param song            the song title
	 * @param startingMeasure the starting measure
	 * @param endingMeasure   the ending measure
	 * @param counts          the counts
	 * @param notes           the notes
	 * @param textPoint       the top-left corner for the page text to be drawn at
	 * @param dots            the map of dots
	 */
	public Page(int number, String song, int startingMeasure, int endingMeasure, int counts, String notes, Point textPoint, PointConcurrentHashMap<Point, String> dots) {
		setNumber(number);
		setSong(song);
		setStartingMeasure(startingMeasure);
		setEndingMeasure(endingMeasure);
		setCounts(counts);
		setNotes(notes);
		setTextPoint(textPoint);
		this.dots = new PointConcurrentHashMap<>(dots);
	}

	/**
	 * Constructs the page object using all the values of another page object
	 *
	 * @param p the other Page
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
	 * Returns the page number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the page number
	 *
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Returns the song title
	 */
	public String getSong() {
		return song;
	}

	/**
	 * Sets the song title
	 *
	 * @param song
	 */
	public void setSong(String song) {
		this.song = song;
	}

	/**
	 * Returns the starting measure number
	 */
	public int getStartingMeasure() {
		return startingMeasure;
	}

	/**
	 * Sets the starting measure number
	 *
	 * @param startingMeasure
	 */
	public void setStartingMeasure(int startingMeasure) {
		this.startingMeasure = startingMeasure;
	}

	/**
	 * Returns the ending measure number
	 */
	public int getEndingMeasure() {
		return endingMeasure;
	}

	/**
	 * Sets the ending measure number
	 *
	 * @param endingMeasure
	 */
	public void setEndingMeasure(int endingMeasure) {
		this.endingMeasure = endingMeasure;
	}

	/**
	 * Returns the counts
	 */
	public int getCounts() {
		return counts;
	}

	/**
	 * Sets the counts
	 *
	 * @param counts
	 */
	public void setCounts(int counts) {
		this.counts = counts;
	}

	/**
	 * Returns the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the notes
	 *
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Returns the the top-left corner for the page text to be drawn at
	 */
	public Point getTextPoint() {
		return textPoint;
	}

	/**
	 * Sets the top-left corner for the page text to be drawn at
	 *
	 * @param textPoint
	 */
	public void setTextPoint(Point textPoint) {
		this.textPoint = textPoint;
	}

	/**
	 * Returns the map of dots
	 */
	public PointConcurrentHashMap<Point, String> getDots() {
		return dots;
	}

	/**
	 * Sets the dots to given dot map
	 *
	 * @param dots
	 */
	public void setDots(PointConcurrentHashMap<Point, String> dots) {
		this.dots = new PointConcurrentHashMap<>(dots);
	}

	/**
	 * The well-fomatted representation of this object
	 *
	 * @return the display string
	 */
	public String toDisplayString() {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(number) + " | " + (song.isEmpty() ? "SongTitle" : song) + ", m." + startingMeasure + "-" + endingMeasure;
	}

	/**
	 * Default toString
	 *
	 * @return String representation of this object
	 */
	@Override public String toString() {
		return "Page [number=" + number + ", song=" + song + ", startingMeasure=" + startingMeasure + ", endingMeasure=" + endingMeasure + ", counts=" + counts + ", notes=" + notes + ", dots=" + dots + "]";
	}

}
