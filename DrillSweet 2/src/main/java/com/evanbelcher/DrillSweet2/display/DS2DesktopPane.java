package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.*;

/**
 * Custom JDesktopPane to display the field
 *
 * @author Evan Belcher
 * @version 1.0
 */
class DS2DesktopPane extends JDesktopPane implements MouseListener {

	private static final long serialVersionUID = -6004681236445735439L;

	private BufferedImage img = null;
	private int imgWidth;
	private int imgHeight;
	private static DS2Rectangle field = new DS2Rectangle(25, 3, 1892 - 25, 982 - 3);
	private static final int dotSize = 9;

	private boolean first = true;
	private Point activePoint;
	private DotDataFrame ddf;
	private PageDataFrame pdf;

	private boolean dragging = false;
	private boolean cancel = false;

	/**
	 * Constructs DS2DesktopPane
	 *
	 * @since 1.0
	 */
	protected DS2DesktopPane() {
		super();
		setFocusable(true);
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		addMouseListener(this);

		pdf = createPageDataFrame();
		createDotDataFrame();
		ddf.setLocation(pdf.getLocation().x, pdf.getLocation().y + pdf.getSize().height);

		activePoint = null;
	}

	/**
	 * Initializes the image from the file. Sets the scaleFactor and field.
	 *
	 * @throws IOException if the file cannot be found
	 * @since 1.0
	 */
	private void getImage() throws IOException {
		img = ImageIO.read(Main.getFile("field.png"));
		double scaleFactor = Math.min(getSize().getWidth() / img.getWidth(), getSize().getHeight() / img.getHeight());
		imgWidth = (int) (img.getWidth() * scaleFactor);
		imgHeight = (int) (img.getHeight() * scaleFactor);
	}

	/**
	 * Sets the field.
	 *
	 * @since 1.0
	 */
	private void getFieldSize() {
		BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		g.drawImage(img, (getSize().width - imgWidth) / 2, (getSize().height - imgHeight) / 2, imgWidth, imgHeight, null);
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
	}

	/**
	 * Creates the Data Frame to hold Point controls
	 *
	 * @since 1.0
	 */
	private void createDotDataFrame() {
		ddf = new DotDataFrame(this);
		ddf.setVisible(true);
		add(ddf);
	}

	/**
	 * Creates the Data Frame to hold Page controls
	 *
	 * @since 1.0
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
	 *
	 * @since 1.0
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, GraphicsRunner.SCREEN_SIZE.width, GraphicsRunner.SCREEN_SIZE.height);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GraphicsRunner.SCREEN_SIZE.width, GraphicsRunner.SCREEN_SIZE.height);

		//if it's the first thing, get the image and get the bounds of the field
		if (first) {
			try {
				getImage();
				getFieldSize();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				first = false;
			}
		}

		if (Main.getState().isShowGrid()) {
			//draw the grid
			g.drawImage(img, (getSize().width - imgWidth) / 2, (getSize().height - imgHeight) / 2, imgWidth, imgHeight, null);

			//draw the text
			if (!PageDataFrame.getDeleting()) {
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
		if (!PageDataFrame.getDeleting() && !DotDataFrame.getDeleting()) {
			for (Point p : Main.getCurrentPage().getDots().keySet()) {
				g.setColor((p.equals(activePoint)) ? Color.RED : Color.BLACK);
				g.fillOval(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize);
				if (Main.getState().isShowNames())
					g.drawString(Main.getCurrentPage().getDots().get(p), p.x, p.y - dotSize / 2);
			}
		}

		//Draw the dragged point
		if (dragging && activePoint != null) {
			g.setColor(Color.RED);
			Point p = MouseInfo.getPointerInfo().getLocation();
			Point q = getLocationOnScreen();
			p.translate(-q.x, -q.y);
			g.fillOval(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize);
		}
	}

	/**
	 * Prints the current page to a png file
	 *
	 * @param makeFolder if a folder titled by the show name should be created
	 * @since 1.0
	 * @deprecated
	 */
	protected void printCurrentPage(boolean makeFolder) {
		activePoint = null;
		ddf.updateAll(activePoint);
		String folder = "";
		if (makeFolder)
			folder = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 5), 0) + "/";

		String fileName = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 5) + ": " + Main.getCurrentPage().toDisplayString().replaceAll("\\|", "-"), 0);
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
	 * @since 1.0
	 */
	protected void printCurrentPageToPdf() throws IOException {
		activePoint = null;
		ddf.updateAll(activePoint);
		String fileName = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 5) + ": " + Main.getCurrentPage().toDisplayString().replaceAll("\\|", "-"), 0);
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
			System.out.println(field);
			for (Point p : Main.getCurrentPage().getDots().keySet())
				if (p.getX() < field.getWidth() * 0.1 + field.getX() || p.getX() > field.getWidth() * 0.9 + field.getX()) {
					System.out.println(p);
					crop = false;
					break;
				}

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
		}
		finally {
			if (doc != null)
				doc.close();
		}
	}

	/**
	 * Prints every page to a pdf file
	 *
	 * @throws IOException if the file cannot be found or the pdf cannot be created
	 * @since 1.0
	 */
	protected void printAllPagesToPdf() throws IOException {
		activePoint = null;
		ddf.updateAll(activePoint);
		File f = new File(Main.getFilePath());
		f.mkdirs();
		String fileName = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 5), 0);

		f = new File(Main.getFilePath() + fileName + " full show" + ".pdf");

		boolean crop = true;
		a:
		for (int pageNum : Main.getPages().keySet()) {
			for (Point p : Main.getPages().get(pageNum).getDots().keySet()) {
				if (p.getX() < field.getWidth() * 0.1 + field.getX() || p.getX() > field.getWidth() * 0.9 + field.getX()) {
					System.out.println(p);
					crop = false;
					break a;
				}
			}
			if (Main.getPages().get(pageNum).getTextPoint().getX() < field.getWidth() * 0.1 + field.getX() || Main.getPages().get(pageNum).getTextPoint().getX() + 100 > field.getWidth() * 0.9 + field.getX()) {
				System.out.println(Main.getPages().get(pageNum).getTextPoint());
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
		}
		finally {
			if (doc != null)
				doc.close();
			pdf.updateAfterPrintAll();
		}
	}

	//Mouselistener

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * On mouse click (down). Adds a new point if there is none or selects the point.
	 *
	 * @since 1.0
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		//Forgive a one-pixel click out of bounds error
		Point clickPoint = new Point(arg0.getPoint());
		if (clickPoint.x == field.width + field.x + 1)
			clickPoint.translate(-1, 0);
		if (clickPoint.y == field.height + field.y + 1)
			arg0.getPoint().translate(0, -1);
		State.print(clickPoint);

		if (field.contains(clickPoint)) {

			if (arg0.getButton() == 1) {
				boolean intersects = false;
				for (Point p : Main.getCurrentPage().getDots().keySet()) {
					if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(arg0.getX(), arg0.getY())) {
						dragging = true;
						activePoint = p;
						intersects = true;
						break;
					}
				}
				if (!intersects) {
					if (activePoint == null) {
						activePoint = arg0.getPoint();
						Main.getCurrentPage().getDots().put(activePoint, "A1");
					} else {
						String str = Main.getCurrentPage().getDots().get(activePoint);
						if (str != null)
							str = str.replaceAll("[0-9]", "") + (Integer.parseInt(str.replaceAll("[A-Za-z]", "")) + 1);
						else
							str = "A1";
						activePoint = arg0.getPoint();
						Main.getCurrentPage().getDots().put(activePoint, str);
					}
				}
				ddf.updateAll(activePoint);
			}
			if (dragging && arg0.getButton() == 3) { //cancel drag
				cancel = true;
				dragging = false;
			}

		} else {
			State.print("click outside boundaries");
		}
	}

	/**
	 * On mouse release (up). Moves selected point if dragged (left click). Removes dot if
	 * right-clicked.
	 *
	 * @since 1.0
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {

		switch (arg0.getButton()) {
			case 1:
				if (dragging) {
					//Move the point to where you released
					int x = Math.min(Math.max(arg0.getX(), field.x), field.width + field.x);
					int y = Math.min(Math.max(arg0.getY(), field.y), field.height + field.y);
					Point p = new Point(x, y);
					String s = Main.getCurrentPage().getDots().get(activePoint);
					Main.getCurrentPage().getDots().remove(activePoint);
					Main.getCurrentPage().getDots().put(p, s);
					activePoint = p;
				}
				ddf.updateAll(activePoint);
				break;
			case 3:
				if (!cancel) {
					//remove the selected point
					boolean intersects = false;
					for (Point p : Main.getCurrentPage().getDots().keySet()) {
						if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(arg0.getX(), arg0.getY())) {
							Main.getCurrentPage().getDots().remove(p);
							if (activePoint == null)
								ddf.updateAll(activePoint);
							if (activePoint.equals(p)) {
								activePoint = null;
								ddf.updateAll(activePoint);
							}
							intersects = true;
							break;
						}
					}
					if (!intersects) {
						activePoint = null;
						ddf.updateAll(activePoint);
					}
				} else //forgive the right click release (dont remove another point)
					cancel = false;
				break;
		}
		dragging = false;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * @return the field boundaries as a rectangle
	 * @since 1.0
	 */
	protected static Rectangle getField() {
		return field;
	}

	/**
	 * @return the active Point.
	 * @since 1.0
	 */
	protected Point getActivePoint() {
		return activePoint;
	}

	/**
	 * Sets the active Point
	 *
	 * @param p new active Point
	 * @since 1.0
	 */
	protected void setActivePoint(Point p) {
		activePoint = p;
	}

}
