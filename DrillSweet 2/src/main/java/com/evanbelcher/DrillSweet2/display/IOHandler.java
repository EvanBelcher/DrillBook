package com.evanbelcher.DrillSweet2.display;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Mouse Listener and Key Bindings for DS2DesktopPane
 */
public class IOHandler implements MouseListener {

	private DS2DesktopPane ddp;
	private boolean normalDragging = false;
	private boolean shiftDragging = false;
	private Point dragStart = null;
	private Vector<Point> activePoints;
	private boolean shiftDown = false;
	private boolean ctrlDown = false;
	private boolean altDown = false;
	private DS2ConcurrentHashMap<Point, String> oldDots;

	/**
	 * Initializes variables and sets up key bindings
	 *
	 * @param ds2DesktopPane
	 */
	public IOHandler(DS2DesktopPane ds2DesktopPane) {
		ddp = ds2DesktopPane;
		activePoints = new Vector<>();
		activePoints.add(null);
		setupKeyBindings();
		updateOldDots();
	}

	/**
	 * Sets up key bindings
	 */
	private void setupKeyBindings() {
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK), "shiftDown");
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), "shiftUp");
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, InputEvent.CTRL_DOWN_MASK), "ctrlDown");
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0, true), "ctrlUp");
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, InputEvent.ALT_DOWN_MASK), "altDown");
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), "altUp");
		ddp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");

		ddp.getActionMap().put("shiftDown", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("shiftDown");
				shiftDown = true;
				ddp.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
		});
		ddp.getActionMap().put("shiftUp", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("shiftUp");
				shiftDown = false;
				ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		ddp.getActionMap().put("ctrlDown", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("controlDown");
				ctrlDown = true;
				ddp.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});
		ddp.getActionMap().put("ctrlUp", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("controlUp");
				ctrlDown = false;
				ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		ddp.getActionMap().put("altDown", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("altDown");
				altDown = true;
				ddp.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			}
		});
		ddp.getActionMap().put("altUp", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("altUp");
				altDown = false;
				ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		ddp.getActionMap().put("delete", new AbstractAction() {

			@Override public void actionPerformed(ActionEvent e) {
				State.print("delete");
				if (activePoints.get(0) != null) {
					updateOldDots();
					for (Point activePoint : activePoints)
						Main.getCurrentPage().getDots().remove(activePoint);
					clearActivePoints();
					ddp.getDotDataFrame().updateAll(activePoints);
				}
			}
		});
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
	 * On mouse click (down). Handles left and right clicks, all depending on which keys are down.
	 */
	@Override public void mousePressed(MouseEvent e) {
		//Forgive a one-pixel click out of bounds error
		Point clickPoint = new Point(e.getPoint());
		if (clickPoint.x == DS2DesktopPane.getField().width + DS2DesktopPane.getField().x + 1)
			clickPoint.translate(-1, 0);
		if (clickPoint.y == DS2DesktopPane.getField().height + DS2DesktopPane.getField().y + 1)
			e.getPoint().translate(0, -1);
		State.print(clickPoint);

		State.print("Shiftdown: " + shiftDown + "\tCtrldown: " + ctrlDown + "\taltdown: " + altDown);

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
										updateOldDots();
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
									updateOldDots();
									for (Point activePoint : activePoints)
										Main.getCurrentPage().getDots().remove(activePoint);
									clearActivePoints();
								} else
									clearActivePoints();
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
								State.print(letter);
								if (activePoints.size() > 1 && activePoints.contains(p)) {
									ListIterator<Point> iterator = activePoints.listIterator();
									while (iterator.hasNext()) {
										if (dotMap.get(iterator.next()).replaceAll("[0-9]", "").equalsIgnoreCase(letter))
											iterator.remove();
									}
									if (activePoints.isEmpty())
										activePoints.add(null);
								} else {
									State.print(letter);
									for (Point q : dotMap.keySet()) {
										if (dotMap.get(q).replaceAll("[0-9]", "").equalsIgnoreCase(letter)) {
											addActivePoint(q);
										}
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
								updateOldDots();
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
				State.print(e.getPoint());
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
										removeActivePoint(p);
										addActivePoint(p);
										ddp.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor"));
									} else {
										clearActivePoints();
										addActivePoint(p);
									}
									break;
								}
							}

							if (!intersects) {
								updateOldDots();
								String str = "A1";
								Point activePoint = e.getPoint();
								if (activePoints.get(0) != null) {
									//									State.print(Main.getCurrentPage().getDots().contains(activePoint));
									str = Main.getCurrentPage().getDots().get(activePoints.get(0));
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
							ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						} else {
							/*
							int dotSize = DS2DesktopPane.getDotSize();
							Iterator<Point> iterator = Main.getCurrentPage().getDots().keySet().iterator();
							while (iterator.hasNext()) {
								Point p = new Point(iterator.next());
								if (activePoints.contains(p))
									clearActivePoints();
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									oldDots = new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots());
									iterator.remove();
									break;
								}
							}
							*/

							int dotSize = DS2DesktopPane.getDotSize();
							boolean intersects = false;
							for (Point p : Main.getCurrentPage().getDots().keySet()) {
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									intersects = true;
									if (activePoints.contains(p)) {
										updateOldDots();
										for (Point activePoint : activePoints)
											Main.getCurrentPage().getDots().remove(activePoint);
										clearActivePoints();
									} else {
										Main.getCurrentPage().getDots().remove(p);
									}
									break;
								}
							}
							if (!intersects)
								clearActivePoints();
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
	 * On mouse release (up). Handles left and right click releases, all depending on which keys are down.
	 */
	@Override public void mouseReleased(MouseEvent e) {
		DS2Rectangle field = DS2DesktopPane.getField();
		int x = Math.min(Math.max(e.getX(), field.x), field.width + field.x);
		int y = Math.min(Math.max(e.getY(), field.y), field.height + field.y);
		Point clickPoint = new Point(x, y);
		if (shiftDragging) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Rectangle rect = new DS2Rectangle(dragStart.x, dragStart.y, clickPoint.x - dragStart.x, clickPoint.y - dragStart.y);
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
				oldDots = new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots());
				Dimension diff = new Dimension(clickPoint.x - dragStart.x, clickPoint.y - dragStart.y);

				ListIterator<Point> iterator = activePoints.listIterator();
				while (iterator.hasNext()) {
					Point activePoint = iterator.next();
					Point p = new Point(activePoint.x + diff.width, activePoint.y + diff.height);
					//Move the point to where you released
					String s = Main.getCurrentPage().getDots().get(activePoint);
					Main.getCurrentPage().getDots().remove(activePoint);
					State.print(p + "    " + s);
					Main.getCurrentPage().getDots().put(p, s);
					iterator.set(p);
				}
			}
			normalDragging = false;
			ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

	/**
	 * Returns if the user is dragging or shift-dragging
	 */
	public boolean isDragging() {
		return shiftDragging || normalDragging;
	}

	/**
	 * Gets activePoints
	 */
	public Vector<Point> getActivePoints() {
		return activePoints;
	}

	/**
	 * Adds the active point. If activePoints was empty (contained null), clear it first
	 *
	 * @param activePoint the point to add
	 */
	public void addActivePoint(Point activePoint) {
		if (activePoints.get(0) == null)
			activePoints.clear();
		activePoints.add(0, activePoint);
	}

	/**
	 * Clears the active points. Adds null.
	 */
	public void clearActivePoints() {
		activePoints.clear();
		activePoints.add(null);
	}

	/**
	 * Removes the given point. Adds null if the list is empty.
	 *
	 * @param p the point to remove
	 */
	public void removeActivePoint(Point p) {
		activePoints.remove(p);
		if (activePoints.isEmpty())
			activePoints.add(null);
	}

	/**
	 * Returns if the user is normal dragging
	 */
	public boolean isNormalDragging() {
		return normalDragging;
	}

	/**
	 * Returns if the user is shift-dragging
	 */
	public boolean isShiftDragging() {
		return shiftDragging;
	}

	/**
	 * Returns the point where the user started dragging
	 */
	public Point getDragStart() {
		return dragStart;
	}

	/**
	 * Resets ctrlDown and cursor
	 */
	public void fixControl() {
		ctrlDown = false;
		ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Sets oldDots to be the current dots before it is changed
	 */
	private void updateOldDots() {
		oldDots = new DS2ConcurrentHashMap<>(Main.getCurrentPage().getDots());
	}

	/**
	 * Deselects all points and returns oldDots
	 *
	 * @return oldDots
	 */
	public DS2ConcurrentHashMap<Point, String> getOldDots() {
		clearActivePoints();
		ddp.getDotDataFrame().updateAll(activePoints);
		return oldDots;
	}
}
