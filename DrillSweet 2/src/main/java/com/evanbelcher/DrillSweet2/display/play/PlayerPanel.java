package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.display.DS2DesktopPane;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class PlayerPanel extends JPanel {

	private DS2DesktopPane desktopPane;
	private BufferedImage fieldImage;
	private Dimension imgSize;
	private HashMap<MovingPoint, String> points;
	private int dotSize;

	public PlayerPanel(DS2DesktopPane desktopPane, HashMap<MovingPoint, String> points) {
		super();
		this.desktopPane = desktopPane;
		this.points = points;
		this.dotSize = DS2DesktopPane.getDotSize();
		fieldImage = desktopPane.getFieldImage();
		imgSize = desktopPane.getImgSize();
		setFocusable(true);
		setSize(desktopPane.getSize());
	}

	@Override public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.drawImage(fieldImage, (getSize().width - imgSize.width) / 2, (getSize().height - imgSize.height) / 2, imgSize.width, imgSize.height, null);

		for (MovingPoint p : points.keySet()) {
			g.setColor(p.getColor());
			g.fillOval(p.current().x - dotSize / 2, p.current().y - dotSize / 2, dotSize, dotSize);
			g.drawString(points.get(p), p.current().x, p.current().y - dotSize / 2);
		}
	}

	public HashMap<MovingPoint, String> getPoints() {
		return points;
	}
}
