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

package com.evanbelcher.DrillSweet2.play;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.data.State;
import com.evanbelcher.DrillSweet2.display.DS2DesktopPane;
import com.evanbelcher.DrillSweet2.play.data.MovingPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * The JDesktopPane that shows the dots
 *
 * @author Evan Belcher
 */
public class PlayerDesktopPane extends JDesktopPane {

	private BufferedImage fieldImage;
	private Dimension imgSize;
	private HashMap<MovingPoint, String> points;
	private int dotSize;

	/**
	 * Creates object. Sets up pane.
	 *
	 * @param pif         The playerInternalFrame to put inside this object
	 * @param desktopPane The DS2DesktopPane to inherit the size from
	 * @param points      The points to display
	 */
	public PlayerDesktopPane(PlayerInternalFrame pif, DS2DesktopPane desktopPane, HashMap<MovingPoint, String> points) {
		super();
		this.points = points;
		this.dotSize = DS2DesktopPane.getDotSize();
		fieldImage = desktopPane.getFieldImage();
		imgSize = desktopPane.getImgSize();
		setFocusable(true);
		setMinimumSize(desktopPane.getSize());
		setMaximumSize(desktopPane.getSize());
		pif.setVisible(true);
		add(pif);
	}

	/**
	 * Draws the points. Does not change their locations.
	 */
	@Override public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (Main.getState().getSettings().shouldShowGrid())
			g.drawImage(fieldImage, (getSize().width - imgSize.width) / 2, (getSize().height - imgSize.height) / 2, imgSize.width, imgSize.height, null);

		for (MovingPoint p : points.keySet()) {
			g.setColor(p.getColor());
			g.fillOval(p.current().x - dotSize / 2, p.current().y - dotSize / 2, dotSize, dotSize);
			if (Main.getState().getSettings().shouldShowNames())
				g.drawString(points.get(p), p.current().x, p.current().y - dotSize / 2);
			if (State.isDebugMode()) {
				Graphics2D g2d = (Graphics2D) g;
				p.drawLine(g2d);
			}
		}
	}

	/**
	 * Returns the points
	 */
	@SuppressWarnings("unused") public HashMap<MovingPoint, String> getPoints() {
		return points;
	}
}
