package main.java.com.evanbelcher.DrillSweet2.display;

import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.DS2ConcurrentHashMap;
import main.java.com.evanbelcher.DrillSweet2.data.State;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Mouse and Key Listeners for DS2DesktopPane
 */
public class IOListener implements MouseListener, KeyListener {

	private DS2DesktopPane ddp;
	private boolean normalDragging = false;
	private boolean shiftDragging = false;
	private Point dragStart = null;
	private Vector<Point> activePoints;
	private boolean shiftDown = false;
	private boolean ctrlDown = false;
	private boolean altDown = false;

	public IOListener(DS2DesktopPane ds2DesktopPane) {
		ddp = ds2DesktopPane;
		activePoints = new Vector<>();
		activePoints.add(null);
	}

	/**
	 * Invoked when a key has been typed.
	 * See the class description for {@link KeyEvent} for a definition of
	 * a key typed event.
	 *
	 * @param e
	 */
	@Override public void keyTyped(KeyEvent e) {

	}

	/**
	 * Invoked when a key has been pressed.
	 * See the class description for {@link KeyEvent} for a definition of
	 * a key pressed event.
	 *
	 * @param e
	 */
	@Override public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				shiftDown = true;
				break;
			case KeyEvent.VK_CONTROL:
				ctrlDown = true;
				break;
			case KeyEvent.VK_ALT:
				altDown = true;
				break;
		}
	}

	/**
	 * Invoked when a key has been released.
	 * See the class description for {@link KeyEvent} for a definition of
	 * a key released event.
	 *
	 * @param e
	 */
	@Override public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				shiftDown = false;
				break;
			case KeyEvent.VK_CONTROL:
				ctrlDown = false;
				break;
			case KeyEvent.VK_ALT:
				altDown = false;
				break;
		}
	}

	/**
	 * Invoked when the mouse button has been clicked (pressed
	 * and released) on a component.
	 *
	 * @param e
	 */
	@Override public void mouseClicked(MouseEvent e) {

	}

	/**
	 * On mouse click (down). Adds a new point if there is none or selects the point.
	 *
	 * @since 1.0.0
	 */
	@Override public void mousePressed(MouseEvent e) {
		//Forgive a one-pixel click out of bounds error
		Point clickPoint = new Point(e.getPoint());
		if (clickPoint.x == DS2DesktopPane.getField().width + DS2DesktopPane.getField().x + 1)
			clickPoint.translate(-1, 0);
		if (clickPoint.y == DS2DesktopPane.getField().height + DS2DesktopPane.getField().y + 1)
			e.getPoint().translate(0, -1);
		State.print(clickPoint);
		if (DS2DesktopPane.getField().contains(clickPoint)) {
			if (shiftDown) {
				switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						shiftDragging = true;
						dragStart = new Point(clickPoint);
						break;
					case MouseEvent.BUTTON3:
						if (isDragging()) {
							shiftDragging = false;
						} else {
							int dotSize = DS2DesktopPane.getDotSize();
							boolean intersects = false;
							for (Point p : Main.getCurrentPage().getDots().keySet()) {
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									intersects = true;
									if (activePoints.contains(p)) {
										for (Point activePoint : activePoints)
											Main.getCurrentPage().getDots().remove(activePoint);
										clearActivePoints();
									}
									break;
								}
							}
							if (!intersects)
								clearActivePoints();
						}
						break;
				}
			} else if (ctrlDown) {
				switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						int dotSize = DS2DesktopPane.getDotSize();
						boolean intersects = false;
						for (Point p : Main.getCurrentPage().getDots().keySet()) {
							if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
								intersects = true;
								if (activePoints.contains(p))
									removeActivePoint(p);
								else
									addActivePoint(p);
								break;
							}
						}
						break;
					case MouseEvent.BUTTON3:
						dotSize = DS2DesktopPane.getDotSize();
						intersects = false;
						for (Point p : Main.getCurrentPage().getDots().keySet()) {
							if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
								intersects = true;
								if (activePoints.contains(p)) {
									for (Point activePoint : activePoints)
										Main.getCurrentPage().getDots().remove(activePoint);
									clearActivePoints();
								}
								break;
							}
						}
						if (!intersects)
							clearActivePoints();
						break;
				}
			} else if (altDown) {
				DS2ConcurrentHashMap<Point, String> dotMap = Main.getCurrentPage().getDots();
				switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						int dotSize = DS2DesktopPane.getDotSize();
						for (Point p : dotMap.keySet()) {
							if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
								String name = dotMap.get(p);
								String letter = name.replaceAll("[0-9]", "");
								for (Point q : dotMap.keySet()) {
									if (dotMap.get(q).replaceAll("[0-9]", "").equals(letter)) {
										addActivePoint(q);
									}
								}
								break;
							}
						}
						break;
					case MouseEvent.BUTTON3:
						dotSize = DS2DesktopPane.getDotSize();
						boolean intersects = false;
						for (Point p : Main.getCurrentPage().getDots().keySet()) {
							if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
								intersects = true;
								String name = dotMap.get(p);
								String letter = name.replaceAll("[0-9]", "");

								if (activePoints.get(0) != null && letter.equalsIgnoreCase(dotMap.get(activePoints.get(0)).replaceAll("[0-9]", "")))
									clearActivePoints();

								dotMap.keySet().removeIf(q -> dotMap.get(q).replaceAll("[0-9]", "").equals(letter));

								break;
							}
						}
						if (!intersects) {
							clearActivePoints();
						}
						break;
				}
			} else {
				switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						if (!isDragging()) {
							int dotSize = DS2DesktopPane.getDotSize();
							boolean intersects = false;
							for (Point p : Main.getCurrentPage().getDots().keySet()) {
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									intersects = true;
									if (activePoints.contains(p)) {
										normalDragging = true;
										dragStart = p;
									} else {
										clearActivePoints();
										addActivePoint(p);
									}
									break;
								}
							}

							if (!intersects) {
								String str = "A1";
								Point activePoint = e.getPoint();
								if (activePoints.get(0) != null) {
									str = Main.getCurrentPage().getDots().get(activePoint);
									if (str != null)
										str = str.replaceAll("[0-9]", "") + (Integer.parseInt(str.replaceAll("[A-Za-z]", "")) + 1);
									else
										str = "A1";
								}
								Main.getCurrentPage().getDots().put(activePoint, str);

								clearActivePoints();
								addActivePoint(activePoint);
							}
						}
						break;
					case MouseEvent.BUTTON3:
						if (isDragging()) {
							normalDragging = false;
						} else {
							int dotSize = DS2DesktopPane.getDotSize();
							Iterator<Point> iterator = Main.getCurrentPage().getDots().keySet().iterator();
							while (iterator.hasNext()) {
								Point p = new Point(iterator.next());
								if (activePoints.contains(p))
									clearActivePoints();
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									iterator.remove();
									break;
								}
							}
						}
						break;
				}
			}
			ddp.getDotDataFrame().updateAll(activePoints);
		} else {
			State.print("click outside boundaries");
		}
	}

	/**
	 * On mouse release (up). Moves selected point if dragged (left click). Removes dot if
	 * right-clicked.
	 *
	 * @since 1.0.0
	 */
	@Override public void mouseReleased(MouseEvent e) {
		DS2Rectangle field = DS2DesktopPane.getField();
		int x = Math.min(Math.max(e.getX(), field.x), field.width + field.x);
		int y = Math.min(Math.max(e.getY(), field.y), field.height + field.y);
		Point clickPoint = new Point(x, y);
		if (shiftDragging) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Rectangle rect = new Rectangle(dragStart, new Dimension(clickPoint.x - dragStart.x, clickPoint.y - dragStart.y));
				int dotSize = DS2DesktopPane.getDotSize();
				for (Point p : Main.getCurrentPage().getDots().keySet()) {
					if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).intersects(rect)) {
						if (!activePoints.contains(p)) {
							addActivePoint(p);
						}
					}
				}
			}
			shiftDragging = false;
		} else if (normalDragging) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				ListIterator<Point> iterator = activePoints.listIterator();
				while (iterator.hasNext()) {
					Point activePoint = iterator.next();
					//Move the point to where you released
					String s = Main.getCurrentPage().getDots().get(activePoint);
					Main.getCurrentPage().getDots().remove(activePoint);
					Main.getCurrentPage().getDots().put(clickPoint, s);
					iterator.set(clickPoint);
				}
			}
			normalDragging = false;
		}
		ddp.getDotDataFrame().updateAll(activePoints);
	}

	/**
	 * Invoked when the mouse enters a component.
	 *
	 * @param e
	 */
	@Override public void mouseEntered(MouseEvent e) {

	}

	/**
	 * Invoked when the mouse exits a component.
	 *
	 * @param e
	 */
	@Override public void mouseExited(MouseEvent e) {

	}

	public boolean isDragging() {
		return shiftDragging || normalDragging;
	}

	public Vector<Point> getActivePoints() {
		return activePoints;
	}

	public void addActivePoint(Point activePoint) {
		if (activePoints.get(0) == null)
			activePoints.clear();
		activePoints.add(0, activePoint);
	}

	public void clearActivePoints() {
		activePoints.clear();
		activePoints.add(null);
	}

	@SuppressWarnings("unused") public void removeActivePoint(Point p) {
		activePoints.remove(p);
		if (activePoints.isEmpty())
			activePoints.add(null);
	}
}
