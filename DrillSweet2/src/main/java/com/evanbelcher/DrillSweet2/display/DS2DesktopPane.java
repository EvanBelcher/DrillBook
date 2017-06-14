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

package com.evanbelcher.DrillSweet2.display;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.data.*;
import com.evanbelcher.DrillSweet2.display.data.DS2Rectangle;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.*;
import java.util.Vector;

/**
 * Custom JDesktopPane to display the field
 *
 * @author Evan Belcher
 */
public class DS2DesktopPane extends JDesktopPane {

	private static final long serialVersionUID = -6004681236445735439L;

	private BufferedImage fieldImage = null;
	private Dimension imgSize;
	private static DS2Rectangle field = new DS2Rectangle(25, 3, 1892 - 25, 982 - 3);
	private static final int DOT_SIZE = 9;

	private boolean first = true;

	private DotDataFrame ddf;
	private PageDataFrame pdf;
	private IOHandler io;

	/**
	 * Constructs DS2DesktopPane
	 */
	protected DS2DesktopPane() {
		super();
		setFocusable(true);
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		io = new IOHandler(this);
		addMouseListener(io);

		pdf = createPageDataFrame();
		createDotDataFrame();
		ddf.setLocation(pdf.getLocation().x, pdf.getLocation().y + pdf.getSize().height);
	}

	/**
	 * Resets the Internal Frames (for file I/O)
	 */
	public void createNewInternalFrames() {
		try {
			io.clearActivePoints();
			pdf.setClosed(true);
			pdf = createPageDataFrame();
			ddf.setClosed(true);
			createDotDataFrame();
			ddf.setLocation(pdf.getLocation().x, pdf.getLocation().y + pdf.getSize().height);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the field.
	 */
	private void getFieldSize() {
		BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.drawImage(fieldImage, (getSize().width - imgSize.width) / 2, (getSize().height - imgSize.height) / 2, imgSize.width, imgSize.height, null);
		g.dispose();

		int startX = 0, startY = 0, endX = 0, endY = 0;

		//Loop diagonally from top-left corner and find red pixel.
		a:
		for (int i = 0; i < bi.getWidth(); i++) {
			for (int x = 0; x <= i; x++) {
				int y = i - x;
				Color c = new Color(bi.getRGB(x, y));
				if (c.getRed() > c.getGreen() + c.getBlue() + 50) {
					startX = x;
					startY = y;
					break a;
				}
			}
		}

		//same, except start at the bottom-right corner
		b:
		for (int i = bi.getWidth() - 1; i >= 0; i--) {
			for (int x = bi.getWidth() - 1; x >= i; x--) {
				int y = bi.getHeight() - 1 - (x - i);
				Color c = new Color(bi.getRGB(x, y));
				if (c.getRed() > c.getGreen() + c.getBlue() + 50) {
					endX = x;
					endY = y;
					break b;
				}
			}
		}

		field = new DS2Rectangle(startX, startY, endX - startX, endY - startY);
		State.print(field);
		Main.getPageMap().fixPoints(field);
	}

	/**
	 * Initializes the image from the file. Sets the scaleFactor and field.
	 *
	 * @throws IOException if the file cannot be found
	 */
	public void getImage() throws IOException {
		fieldImage = ImageIO.read(Main.getFile(Main.getState().getSettings().useCollegeHashes() ? "clgfield.png" : "hsfield.png", this));
		double scaleFactor = Math.min(getSize().getWidth() / fieldImage.getWidth(), getSize().getHeight() / fieldImage.getHeight());
		imgSize = new Dimension((int) (fieldImage.getWidth() * scaleFactor), (int) (fieldImage.getHeight() * scaleFactor));
	}

	/**
	 * Creates the Data Frame to hold Point controls
	 */
	private void createDotDataFrame() {
		ddf = new DotDataFrame(this);
		ddf.setVisible(true);
		add(ddf);
	}

	/**
	 * Creates the Data Frame to hold Page controls
	 */
	private PageDataFrame createPageDataFrame() {
		PageDataFrame frame = new PageDataFrame();
		frame.setVisible(true);
		add(frame);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}
		return frame;
	}

	/**
	 * Paints the dots, dot names, and page info
	 */
	@Override public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		//if it's the first thing, get the image and get the bounds of the field
		if (first) {
			try {
				getImage();
				getFieldSize();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				first = false;
			}
		}

		if (Main.getState().getSettings().shouldShowGrid()) {
			//draw the grid
			g.drawImage(fieldImage, (getSize().width - imgSize.width) / 2, (getSize().height - imgSize.height) / 2, imgSize.width, imgSize.height, null);
		}
		if (Main.getState().getSettings().shouldShowText()) {
			//draw the text
			if (PageDataFrame.getNotDeleting()) {
				Page p = Main.getCurrentPage();
				String str = "Page " + p.getNumber() + " - " + p.getSong() + "\nMeasures " + p.getStartingMeasure() + "-" + p.getEndingMeasure() + ", " + p.getCounts() + " count" + (p.getCounts() == 1 ? "" : "s") + "\n" + p.getNotes();
				String[] lines = str.split("\\n");

				Font oldFont = g.getFont();
				g.setFont(new Font(oldFont.getFontName(), oldFont.getStyle(), 16));

				int height = g.getFontMetrics().getHeight();
				int width = Integer.MIN_VALUE;
				for (String s : lines)
					width = Math.max(width, g.getFontMetrics().stringWidth(s));
				width += 10;
				g.setColor(Color.WHITE);
				Point textPoint = Main.getCurrentPage().getTextPoint();
				g.fillRect(textPoint.x, textPoint.y, width, height * lines.length + 5);
				g.setColor(Color.BLACK);
				g.drawRect(textPoint.x, textPoint.y, width, height * lines.length + 5);

				for (int i = 1; i <= lines.length; i++) {
					g.drawString(lines[i - 1], textPoint.x + 5, textPoint.y + height * i);
				}
				g.setFont(oldFont);
			}
		}

		//Draw the points and their names
		if (PageDataFrame.getNotDeleting() && !DotDataFrame.isDeleting()) {
			for (Point p : Main.getCurrentPage().getDots().keySet()) {
				//g.setColor((io.getActivePoints().contains(p)) ? (io.isNormalDragging() ? Color.PINK : Color.RED) : Color.BLACK);
				float hue = Character.getNumericValue(Main.getCurrentPage().getDots().get(p).charAt(0)) - 65;
				hue *= 1.0 / 26.0;
				g.setColor((io.getActivePoints().contains(p)) ? (io.isNormalDragging() ? Color.PINK : Color.RED) : Main.getState().getSettings().shouldColorDots() ? new Color(Color.HSBtoRGB(hue, 1, 0.7f)) : Color.BLACK);
				g.fillOval(p.x - DOT_SIZE / 2, p.y - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);

				if (Main.getState().getSettings().shouldShowNames())
					g.drawString(Main.getCurrentPage().getDots().get(p), p.x, p.y - DOT_SIZE / 2);
			}
		}

		//Draw the dragged point
		if (io.isNormalDragging()) {
			g.setColor(Color.RED);
			Point p = MouseInfo.getPointerInfo().getLocation();
			Point q = getLocationOnScreen();
			p.translate(-q.x, -q.y);
			Point activePoint = getActivePoints().get(0);
			Dimension diff = new Dimension(p.x - activePoint.x, p.y - activePoint.y);
			for (Point ap : io.getActivePoints())
				g.fillOval((ap.x + diff.width) - DOT_SIZE / 2, (ap.y + diff.height) - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
		}
		if (io.isShiftDragging()) {
			g.setColor(Color.BLACK);
			Graphics2D g2d = (Graphics2D) g;
			final float dash1[] = { 10.0f };
			final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
			Stroke old = g2d.getStroke();
			g2d.setStroke(dashed);
			Point p = MouseInfo.getPointerInfo().getLocation();
			Point q = getLocationOnScreen();
			p.translate(-q.x, -q.y);
			g2d.draw(new DS2Rectangle(io.getDragStart().x, io.getDragStart().y, p.x - io.getDragStart().x, p.y - io.getDragStart().y));
			g2d.setStroke(old);
		}
	}

	/**
	 * Prints the current page to a png file
	 *
	 * @param makeFolder if a folder titled by the show name should be created
	 * @deprecated
	 */
	@SuppressWarnings("unused") protected void printCurrentPage(boolean makeFolder) {
		io.clearActivePoints();
		ddf.updateAll(io.getActivePoints());
		String folder = "";
		if (makeFolder)
			folder = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 4)) + "/";

		String fileName = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 4) + ": " + Main.getCurrentPage().toDisplayString().replaceAll("\\|", "-"));
		File f = new File(Main.getFilePath() + folder + fileName + ".png");
		f.mkdirs();

		BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		paintComponent(g);
		g.dispose();
		try {
			ImageIO.write(bi, "png", f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the current page to a pdf file
	 *
	 * @throws IOException if the file cannot be found or the pdf cannot be created
	 */
	protected void printCurrentPageToPdf() throws IOException {
		io.clearActivePoints();
		ddf.updateAll(io.getActivePoints());
		String fileName = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 4) + ": " + Main.getCurrentPage().toDisplayString().replaceAll("\\|", "-"));
		File f = new File(Main.getFilePath());
		f.mkdirs();
		f = new File(Main.getFilePath() + fileName + ".pdf");

		BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		paintComponent(g);
		g.dispose();

		PDDocument doc = null;

		try {
			doc = new PDDocument();
			boolean crop = true;
			State.print(field);
			for (Point p : Main.getCurrentPage().getDots().keySet())
				if (p.getX() < field.getWidth() * 0.1 + field.getX() || p.getX() > field.getWidth() * 0.9 + field.getX()) {
					crop = false;
					break;
				}
			if (Main.getCurrentPage().getTextPoint().getX() < field.getWidth() * 0.1 + field.getX() || Main.getCurrentPage().getTextPoint().getX() + 100 > field.getWidth() * 0.9 + field.getX())
				crop = false;

			float scale = 1.0f;
			if (crop)
				scale = 0.8f;

			PDPage page = new PDPage(new PDRectangle((float) field.getWidth() * scale, (float) field.getHeight()));
			doc.addPage(page);
			PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bi);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true);

			contentStream.drawImage(pdImage, -1 * field.x - (float) (((1 - scale) / 2.0f) * field.getWidth()), -1 * field.y, pdImage.getWidth(), pdImage.getHeight());

			contentStream.close();
			doc.save(f);
		} finally {
			if (doc != null)
				doc.close();
		}
	}

	/**
	 * Prints every page to a pdf file
	 *
	 * @throws IOException if the file cannot be found or the pdf cannot be created
	 */
	protected void printAllPagesToPdf() throws IOException {
		io.clearActivePoints();
		ddf.updateAll(io.getActivePoints());
		File f = new File(Main.getFilePath());
		f.mkdirs();
		String fileName = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 4));

		f = new File(Main.getFilePath() + fileName + " full show" + ".pdf");

		boolean crop = true;
		a:
		for (int pageNum : Main.getPages().keySet()) {
			for (Point p : Main.getPages().get(pageNum).getDots().keySet()) {
				if (p.getX() < field.getWidth() * 0.1 + field.getX() || p.getX() > field.getWidth() * 0.9 + field.getX()) {
					crop = false;
					break a;
				}
			}
			if (Main.getPages().get(pageNum).getTextPoint().getX() < field.getWidth() * 0.1 + field.getX() || Main.getPages().get(pageNum).getTextPoint().getX() + 100 > field.getWidth() * 0.9 + field.getX()) {
				crop = false;
				break;
			}
		}

		float scale = 1.0f;
		if (crop)
			scale = 0.8f;
		PDDocument doc = null;

		try {
			doc = new PDDocument();

			for (int i : Main.getPages().keySet()) {
				Main.setCurrentPage(i);

				BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
				Graphics g = bi.createGraphics();
				paintComponent(g);
				g.dispose();

				PDPage page = new PDPage(new PDRectangle((float) field.getWidth() * scale, (float) field.getHeight()));
				doc.addPage(page);
				PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bi);
				PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true);

				contentStream.drawImage(pdImage, -1 * field.x - (float) (((1 - scale) / 2.0f) * field.getWidth()), -1 * field.y, pdImage.getWidth(), pdImage.getHeight());

				contentStream.close();
			}
			doc.save(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (doc != null)
				doc.close();
			pdf.updateAfterPrintAll();
		}
	}

	/**
	 * @return the field boundaries as a rectangle
	 */
	public static DS2Rectangle getField() {
		return field;
	}

	/**
	 * Returns the active Points
	 */
	protected Vector<Point> getActivePoints() {
		return io.getActivePoints();
	}

	/**
	 * Adds a new active point
	 *
	 * @param p new active Point
	 */
	protected void addActivePoint(Point p) {
		io.addActivePoint(p);
	}

	/**
	 * Clears the active points
	 */
	@SuppressWarnings("unused") public void clearActivePoints() {
		io.clearActivePoints();
	}

	/**
	 * Returns the dot size
	 */
	public static int getDotSize() {
		return DOT_SIZE;
	}

	/**
	 * Returns the dot data frame
	 */
	public DotDataFrame getDotDataFrame() {
		return ddf;
	}

	/**
	 * Returns the IOHandler
	 */
	public IOHandler getIO() {
		return io;
	}

	/**
	 * Returns the field image
	 */
	public BufferedImage getFieldImage() {
		return fieldImage;
	}

	/**
	 * Returns the size of the field image
	 */
	public Dimension getImgSize() {
		return imgSize;
	}

}
