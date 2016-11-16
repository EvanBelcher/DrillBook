
package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.*;

/**
 * Custom JDesktopPane to display the field
 * 
 * @author Evan Belcher
 * @version 1.0
 */
public class DS2DesktopPane extends JDesktopPane implements MouseListener {
	
	private static final long serialVersionUID = -6004681236445735439L;
	
	private BufferedImage img = null;
	private int imgWidth;
	private int imgHeight;
	private static DS2Rectangle field = new DS2Rectangle(25, 3, 1892 - 25, 982 - 3);
	private static final int dotSize = 9;
	
	private boolean first = true;
	private Point activePoint;
	private DotDataFrame ddf;
	
	private boolean dragging = false;
	private boolean mouseOk = true;
	
	/**
	 * Constructs DS2DesktopPane
	 * 
	 * @since 1.0
	 */
	public DS2DesktopPane() throws IOException {
		super();
		setFocusable(true);
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		addMouseListener(this);
		createPageDataFrame();
		createDotDataFrame();
		activePoint = null;
	}
	
	/**
	 * Initializes the image from the file. Sets the scaleFactor and field.
	 * 
	 * @throws IOException
	 *             if the file cannot be found
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
		
		//		System.out.println(bi.getWidth() + " " + bi.getHeight());
		
		a:
		for (int i = 0; i < bi.getWidth(); i++) {
			for (int x = 0; x <= i; x++) {
				int y = i - x;
				Color c = new Color(bi.getRGB(x, y));
				//				System.out.println("beginning " + i + " " + x + " " + y + " " + c);
				if (c.getRed() > c.getGreen() + c.getBlue() + 50) {
					startX = x;
					startY = y;
					break a;
				}
			}
		}
		
		b:
		for (int i = bi.getWidth() - 1; i >= 0; i--) {
			for (int x = bi.getWidth() - 1; x >= i; x--) {
				int y = bi.getHeight() - 1 - (x - i);
				//				System.out.print("end " + i + " " + x + " " + y + " ");
				Color c = new Color(bi.getRGB(x, y));
				//				System.out.println(c);
				if (c.getRed() > c.getGreen() + c.getBlue() + 50) {
					endX = x;
					endY = y;
					break b;
				}
			}
		}
		
		//scaleFactor = scaleFactor / 0.7426415094339622;
		//field = new DS2Rectangle(field.x * scaleFactor, field.y * scaleFactor, field.width * scaleFactor, field.height * scaleFactor);
		field = new DS2Rectangle(startX, startY, endX - startX, endY - startY);
		
		System.out.println(field);
		//field = new Rectangle(field.x * getSize().getWidth() / 1914, field.y * getSize().getHeight() / 984)
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
	private void createPageDataFrame() {
		PageDataFrame frame = new PageDataFrame();
		frame.setVisible(true);
		add(frame);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {}
	}
	
	/**
	 * Paints the dots, dot names, and page info
	 * 
	 * @since 1.0
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, GraphicsRunner.SCREEN_SIZE.width, GraphicsRunner.SCREEN_SIZE.height);
		if (Main.getState().isShowGrid()) {
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
			
			g.drawImage(img, (getSize().width - imgWidth) / 2, (getSize().height - imgHeight) / 2, imgWidth, imgHeight, null);
			
			if (!PageDataFrame.getDeleting()) {
				Page p = Main.getCurrentPage();
				String str = "Page " + p.getNumber() + " - " + p.getSong() + "\nMeasures " + p.getStartingMeasure() + "-" + p.getEndingMeasure() + ", " + p.getCounts() + " count" + (p.getCounts() == 1 ? "" : "s") + "\n" + p.getNotes();
				String[] lines = str.split("\\n");
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
			}
			
		}
		
		if (!PageDataFrame.getDeleting() && !DotDataFrame.getDeleting()) {
			for (Point p : Main.getCurrentPage().getDots().keySet()) {
				g.setColor((p.equals(activePoint)) ? Color.RED : Color.BLACK);
				g.fillOval(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize);
				if (Main.getState().isShowNames())
					g.drawString(Main.getCurrentPage().getDots().get(p), p.x, p.y - dotSize / 2);
			}
		}
	}
	
	/**
	 * Prints the current page to a png file
	 * 
	 * @param makeFolder
	 *            if a folder titled by the show name should be created
	 * @since 1.0
	 */
	public void printCurrentPage(boolean makeFolder) {
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
		} catch (Exception e) {}
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
		State.print(arg0.getX() + " " + arg0.getY());
		Point clickPoint = new Point(arg0.getPoint());
		if (clickPoint.x == field.width + field.x + 1)
			clickPoint.translate(-1, 0);
		if (clickPoint.y == field.height + field.y + 1)
			arg0.getPoint().translate(0, -1);
		State.print(clickPoint);
		if (field.contains(clickPoint)) {
			while (mouseOk == false) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (arg0.getButton() == 1) {
				boolean b = true;
				for (Point p : Main.getCurrentPage().getDots().keySet()) {
					if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(arg0.getX(), arg0.getY())) {
						dragging = true;
						activePoint = p;
						b = false;
						break;
					}
				}
				if (b) {
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
			
			mouseOk = true;
			
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
		while (mouseOk == false) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		switch (arg0.getButton()) {
			case 1:
				if (dragging) {
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
				boolean intersects = false;
				Point q = null;
				for (Point p : Main.getCurrentPage().getDots().keySet()) {
					if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(arg0.getX(), arg0.getY())) {
						intersects = true;
						q = p;
						break;
					}
				}
				if (intersects) {
					Main.getCurrentPage().getDots().remove(q);
					if (activePoint == null)
						ddf.updateAll(activePoint);
					if (activePoint.equals(q)) {
						activePoint = null;
						ddf.updateAll(activePoint);
					}
				}
				break;
		}
		mouseOk = true;
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
	public static Rectangle getField() {
		return field;
	}
	
	/**
	 * @return the active Point.
	 * @since 1.0
	 */
	public Point getActivePoint() {
		return activePoint;
	}
	
	/**
	 * Sets the active Point
	 * 
	 * @param new
	 *            active Point
	 * @since 1.0
	 */
	public void setActivePoint(Point p) {
		activePoint = p;
	}
	
}
