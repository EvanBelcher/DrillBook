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

package com.evanbelcher.DrillBook.display;

import com.evanbelcher.DrillBook.Main;
import com.evanbelcher.DrillBook.data.*;
import com.evanbelcher.DrillBook.display.data.DBRectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Mouse Listener and Key Bindings for DBDesktopPane
 *
 * @author Evan Belcher
 */
public class IOHandler implements MouseListener {

	private DBDesktopPane ddp;
	private boolean normalDragging = false;
	private boolean shiftDragging = false;
	private Point dragStart = null;
	private Vector<Point> activePoints;
	private boolean shiftDown = false;
	private boolean ctrlDown = false;
	private boolean altDown = false;

	/**
	 * Initializes variables and sets up key bindings
	 *
	 * @param dbDesktopPane
	 */
	public IOHandler(DBDesktopPane dbDesktopPane) {
		ddp = dbDesktopPane;
		activePoints = new Vector<>();
		activePoints.add(null);
		setupKeyBindings();
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
					updateHistory();
					for (Point activePoint : activePoints)
						Main.getCurrentPage().getDots().remove(activePoint);
					clearActivePoints();
					ddp.getDotDataFrame().updateAll(activePoints);
					updatePresent();
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
		if (clickPoint.x == DBDesktopPane.getField().width + DBDesktopPane.getField().x + 1)
			clickPoint.translate(-1, 0);
		if (clickPoint.y == DBDesktopPane.getField().height + DBDesktopPane.getField().y + 1)
			e.getPoint().translate(0, -1);
		State.print(clickPoint);

		State.print("Shiftdown: " + shiftDown + "\tCtrldown: " + ctrlDown + "\taltdown: " + altDown);

		if (DBDesktopPane.getField().contains(clickPoint)) {
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
							int dotSize = DBDesktopPane.getDotSize();
							boolean intersects = false;
							for (Point p : Main.getCurrentPage().getDots().keySet()) {
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									intersects = true;
									if (activePoints.contains(p)) {
										updateHistory();
										for (Point activePoint : activePoints)
											Main.getCurrentPage().getDots().remove(activePoint);
										clearActivePoints();
										updatePresent();
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
						int dotSize = DBDesktopPane.getDotSize();
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
						dotSize = DBDesktopPane.getDotSize();
						intersects = false;
						for (Point p : Main.getCurrentPage().getDots().keySet()) {
							if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
								intersects = true;
								if (activePoints.contains(p)) {
									updateHistory();
									for (Point activePoint : activePoints)
										Main.getCurrentPage().getDots().remove(activePoint);
									clearActivePoints();
									updatePresent();
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
				PointConcurrentHashMap<Point, String> dotMap = Main.getCurrentPage().getDots();
				switch (e.getButton()) {
					case MouseEvent.BUTTON1:
						int dotSize = DBDesktopPane.getDotSize();
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
						dotSize = DBDesktopPane.getDotSize();
						boolean intersects = false;
						for (Point p : Main.getCurrentPage().getDots().keySet()) {
							if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
								updateHistory();
								intersects = true;
								String name = dotMap.get(p);
								String letter = name.replaceAll("[0-9]", "");

								if (activePoints.get(0) != null && letter.equalsIgnoreCase(dotMap.get(activePoints.get(0)).replaceAll("[0-9]", "")))
									clearActivePoints();

								dotMap.keySet().removeIf(q -> dotMap.get(q).replaceAll("[0-9]", "").equals(letter));
								updatePresent();
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
							int dotSize = DBDesktopPane.getDotSize();
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
								updateHistory();
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
								updatePresent();
							}
						}
						break;
					case MouseEvent.BUTTON3:
						if (isDragging()) {
							normalDragging = false;
							ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						} else {
							int dotSize = DBDesktopPane.getDotSize();
							boolean intersects = false;
							for (Point p : Main.getCurrentPage().getDots().keySet()) {
								if (new Rectangle(p.x - dotSize / 2, p.y - dotSize / 2, dotSize, dotSize).contains(clickPoint)) {
									intersects = true;
									updateHistory();
									if (activePoints.contains(p)) {
										for (Point activePoint : activePoints)
											Main.getCurrentPage().getDots().remove(activePoint);
										clearActivePoints();
									} else {
										Main.getCurrentPage().getDots().remove(p);
									}
									updatePresent();
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
		DBRectangle field = DBDesktopPane.getField();
		int x = Math.min(Math.max(e.getX(), field.x), field.width + field.x);
		int y = Math.min(Math.max(e.getY(), field.y), field.height + field.y);
		Point clickPoint = new Point(x, y);
		if (shiftDragging) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Rectangle rect = new DBRectangle(dragStart.x, dragStart.y, clickPoint.x - dragStart.x, clickPoint.y - dragStart.y);
				int dotSize = DBDesktopPane.getDotSize();
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
				updateHistory();
				Dimension diff = new Dimension(clickPoint.x - dragStart.x, clickPoint.y - dragStart.y);

				Dimension oobFix = fixOutOfBounds(diff);

				ListIterator<Point> iterator = activePoints.listIterator();
				while (iterator.hasNext()) {
					Point activePoint = iterator.next();
					Point p = new Point(activePoint.x + diff.width + oobFix.width, activePoint.y + diff.height + oobFix.height);
					//Move the point to where you released
					String s = Main.getCurrentPage().getDots().get(activePoint);
					Main.getCurrentPage().getDots().remove(activePoint);
					State.print(p + "    " + s);
					Main.getCurrentPage().getDots().put(p, s);
					iterator.set(p);
				}
				updatePresent();
			}
			normalDragging = false;
			ddp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		ddp.getDotDataFrame().updateAll(activePoints);
	}

	/**
	 * Prevents user from dragging points off of the field.
	 * Makes assumption that neither the domain nor the range of points can be wider/taller than the field itself
	 *
	 * @param diff the offset to the prejected new locations of the points
	 * @return the offset required to keep all points on the field
	 */
	private Dimension fixOutOfBounds(Dimension diff) {
		Dimension ret = new Dimension(0, 0);

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		for (Point p : activePoints) {
			minX = Math.min(minX, p.x + diff.width);
			minY = Math.min(minY, p.y + diff.height);
			maxX = Math.max(maxX, p.x + diff.width);
			maxY = Math.max(maxY, p.y + diff.height);
		}

		Rectangle field = DBDesktopPane.getField();
		if (minX < field.x)
			ret.width = field.x - minX;
		else if (maxX > field.x + field.width)
			ret.width = field.x + field.width - maxX;

		if (minY < field.y)
			ret.height = field.y - minY;
		else if (maxY > field.y + field.height)
			ret.height = field.y + field.height - maxY;

		return ret;
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
	 * Adds to undo queue
	 */
	private void updateHistory() {
		Main.getState().addHistory();
	}

	/**
	 * Adds to redo queue
	 */
	private void updatePresent() {
		Main.getState().addFuture();
	}
}
