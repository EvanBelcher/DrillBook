
package main.java.com.evanbelcher.DrillSweet2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import main.java.com.evanbelcher.DrillSweet2.data.DS2ConcurrentHashMap;
import main.java.com.evanbelcher.DrillSweet2.display.*;

/**
 * Creates dot sheets for all dots
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class DotSheetMaker extends JPanel {
	
	private static final long serialVersionUID = 6371967053629285090L;
	ConcurrentHashMap<String, HashMap<Integer, String>> map;
	HashMap<Integer, String> currentMap;
	String currentName;
	final int WIDTH = 300;
	final int HEIGHT = 1600;
	
	/**
	 * Constructs object. Automatically runs getDotSheetData().
	 * @since 1.0
	 */
	public DotSheetMaker() {
		setSize(WIDTH, HEIGHT);
		getDotSheetData();
	}
	
	/**
	 * Gets data for the dot sheet and stores it in map.
	 * @since 1.0
	 */
	private void getDotSheetData() {
		map = new ConcurrentHashMap<>();
		for (int i = 1; i <= Main.getPages().size(); i++) {
			DS2ConcurrentHashMap<Point, String> dots = Main.getPages().get(i).getDots();
			for (Point p : dots.keySet()) {
				String s = dots.get(p);
				if (!map.containsKey(s))
					map.put(s, new HashMap<>());
				map.get(s).put(i, DotDataFrame.getPointText(p));
			}
		}
	}
	
	/**
	 * Prints an individual dot sheet for the currentName and currentMap
	 * @since 1.0
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		int height = g.getFontMetrics().getHeight();
		int y = height;
		
		String str = currentName + "           Name: ";
		
		g.setColor(Color.BLACK);
		g.drawString(str, 5, y);
		
		y += height * 2;
		
		for (int i : currentMap.keySet()) {
			str = "Set " + i + ":    " + currentMap.get(i).replaceAll("\\n", "\n               ");
			String[] lines = str.split("\\n");
			
			g.setColor(Color.BLACK);
			
			for (int j = 1; j <= lines.length; j++) {
				if (j % 2 == 1) {
					g.setColor(Color.YELLOW);
					g.fillRect(0, y, WIDTH, height);
					g.setColor(Color.BLACK);
				}
				g.drawString(lines[j - 1], 5, y);
				y += height;
			}
		}
	}
	
	/**
	 * Prints all dot sheets
	 * @param
	 * @return
	 * @throws
	 * @since 1.0
	 */
	public void printAll() {
		int height = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics().getHeight();
		String folder = "";
		folder = DS2MenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 5), 0) + "/Dot Sheets/";
		File f = new File(folder);
		f.mkdirs();
		for (String s : map.keySet()) {
			String fileName = DS2MenuBar.cleanseFileName(s, 0);
			f = new File(Main.getFilePath() + folder + fileName + ".png");
			setSize(WIDTH, height * (3 + map.get(s).size() * 2));
			
			currentName = s;
			currentMap = map.get(s);
			
			BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			paintComponent(g);
			g.dispose();
			f.mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				ImageIO.write(bi, "png", f);
			} catch (Exception e) {}
		}
	}
}
