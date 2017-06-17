/*
		DrillBook is a marching band drill creation software.
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

package com.evanbelcher.DrillBook;

import be.quodlibet.boxable.*;
import com.evanbelcher.DrillBook.data.PointConcurrentHashMap;
import com.evanbelcher.DrillBook.display.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Creates dot sheets for all dots
 *
 * @author Evan Belcher
 */
public class DotSheetMaker extends JPanel {

	private static final long serialVersionUID = 6371967053629285090L;
	private static boolean printing = false;
	private final int WIDTH = 300;
	private final int HEIGHT = 1600;
	private HashMap<String, HashMap<Integer, String>> map;
	private HashMap<Integer, String> currentMap;
	private ArrayList<Integer> openingSets;
	private String currentName;
	private Comparator<String> nameComparator = (String o1, String o2) -> {
		String name1 = o1.replaceAll("[0-9]", "");
		String name2 = o2.replaceAll("[0-9]", "");
		int num1 = Integer.parseInt(o1.replaceAll("[A-Za-z]", ""));
		int num2 = Integer.parseInt(o2.replaceAll("[A-Za-z]", ""));
		if (!name1.equals(name2))
			return name1.compareTo(name2);
		return (num1 > num2) ? 1 : -1;
	};
	private final Color HEADER_COLOR = Color.CYAN;
	private final Color OPENING_SET_COLOR = Color.YELLOW;
	private final Color NORMAL_COLOR = Color.WHITE;

	/**
	 * Constructs object. Automatically runs getDotSheetData().
	 */
	public DotSheetMaker() {
		setSize(WIDTH, HEIGHT);
		getDotSheetData();
	}

	/**
	 * Returns printing
	 */
	public static boolean isPrinting() {
		return printing;
	}

	/**
	 * Gets data for the dot sheet and stores it in map.
	 */
	private void getDotSheetData() {
		map = new HashMap<>();
		openingSets = new ArrayList<>();
		String currentSong = "";
		for (int i = 1; i <= Main.getPages().size(); i++) {
			PointConcurrentHashMap<Point, String> dots = Main.getPages().get(i).getDots();
			if (!currentSong.equals(Main.getPages().get(i).getSong())) {
				currentSong = Main.getPages().get(i).getSong();
				openingSets.add(i);
			}
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
	 *
	 * @deprecated
	 */
	@Override public void paintComponent(Graphics g) {
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
	 * Prints all dot sheets to png files
	 *
	 * @deprecated
	 */
	@SuppressWarnings("unused") public void printAll() {
		int height = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics().getHeight();
		String folder = DBMenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 6)) + "/Dot Sheets/";
		File f = new File(Main.getFilePath() + folder);
		f.mkdirs();
		for (String s : map.keySet()) {
			String fileName = DBMenuBar.cleanseFileName(s);
			f = new File(Main.getFilePath() + folder + fileName + ".png");
			setSize(WIDTH, height * (3 + map.get(s).size() * 2));

			currentName = s;
			currentMap = map.get(s);

			BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.createGraphics();
			//noinspection deprecation
			paintComponent(g);
			g.dispose();
			try {
				ImageIO.write(bi, "png", f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Prints all dot sheets to pdf files
	 */
	private void printAllToPdf() throws IOException {
		printing = true;
		String folder = DBMenuBar.cleanseFileName(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 6)) + " Dot Sheets/";
		File f = new File(Main.getFilePath() + folder);
		f.mkdirs();
		PDDocument doc = null;

		String[] chars = new String[26];
		for (int i = 0; i < 26; i++)
			chars[i] = String.valueOf((char) (65 + i));

		try {
			for (String letter : chars) {
				doc = new PDDocument();

				String fileName = Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 6) + " " + DBMenuBar.cleanseFileName(letter);
				f = new File(Main.getFilePath() + folder + fileName + " dot sheet.pdf");

				ArrayList<String> list = new ArrayList<>(map.keySet());
				list.sort(nameComparator);

				for (String dotName : list) {
					if (dotName.replaceAll("[0-9]", "").equals(letter)) {
						int i = 0;

						PDPage page = new PDPage();
						doc.addPage(page);

						PDFont font = PDType1Font.HELVETICA_BOLD;
						PDPageContentStream contentStream = new PDPageContentStream(doc, page);
						contentStream.beginText();
						contentStream.setFont(font, 10.0f);
						contentStream.newLineAtOffset(10, page.getMediaBox().getHeight() - 20);
						contentStream.showText(Main.getState().getCurrentFileName().substring(0, Main.getState().getCurrentFileName().length() - 6));
						contentStream.endText();

						contentStream.beginText();
						contentStream.setFont(font, 12.0f);
						contentStream.newLineAtOffset(page.getMediaBox().getWidth() * 0.3f, page.getMediaBox().getHeight() - 20);
						contentStream.showText(dotName);
						contentStream.endText();

						contentStream.beginText();
						contentStream.setFont(font, 10.0f);
						contentStream.newLineAtOffset(page.getMediaBox().getWidth() * 0.6f, page.getMediaBox().getHeight() - 20);
						contentStream.showText("Name: ______________________________");
						contentStream.endText();
						contentStream.close();

						float margin = 10;
						float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
						float yStartNewPage = page.getMediaBox().getHeight() - (3 * margin);
						//noinspection UnnecessaryLocalVariable
						float yStart = yStartNewPage;
						float bottomMargin = 70;

						BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);
						//Create Header row
						Row<PDPage> headerRow = table.createRow(15f);
						Cell<PDPage> headerCell = headerRow.createCell(100 / 7f, "Set #");
						headerCell.setAlign(HorizontalAlignment.CENTER);
						headerCell.setFillColor(HEADER_COLOR);
						headerRow.createCell(300 / 7f, "Horizontal").copyCellStyle(headerCell);
						headerRow.createCell(300 / 7f, "Vertical").copyCellStyle(headerCell);

						table.addHeaderRow(headerRow);
						for (int pageNum : new TreeSet<>(map.get(dotName).keySet())) {
							String text = map.get(dotName).get(pageNum);
							String[] lines = text.split("\\n");
							String line1 = lines[0].replace("Horizontal - ", "");
							String line2 = lines[1].replace("Vertical - ", "");

							Row<PDPage> row = table.createRow(10f);
							Cell<PDPage> cell = row.createCell(100 / 7f, pageNum + "");
							cell.setAlign(HorizontalAlignment.CENTER);
							cell.setFillColor(openingSets.contains(pageNum) ? OPENING_SET_COLOR : NORMAL_COLOR);
							row.createCell(300 / 7f, line1).copyCellStyle(cell);
							row.createCell(300 / 7f, line2).copyCellStyle(cell);

							if (++i >= 35) {
								table.draw();
								page = new PDPage();
								doc.addPage(page);
								table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);
								//Create Header row
								headerRow = table.createRow(15f);
								headerCell = headerRow.createCell(100 / 7f, "Set #");
								headerCell.setAlign(HorizontalAlignment.CENTER);
								headerCell.setFillColor(HEADER_COLOR);
								headerRow.createCell(300 / 7f, "Horizontal").copyCellStyle(headerCell);
								headerRow.createCell(300 / 7f, "Vertical").copyCellStyle(headerCell);
								table.addHeaderRow(headerRow);

								i -= 35;
							}
						}
						table.draw();

					}
				}
				if (doc.getNumberOfPages() > 0)
					doc.save(f);
				else
					doc.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (doc != null)
				doc.close();
		}
		printing = false;
	}

	/**
	 * Print all dot sheets
	 *
	 * @throws InterruptedException if the thread fails for some reason
	 */
	public void printDotSheets() throws InterruptedException {
		Thread t = new Thread(() -> {
			try {
				printAllToPdf();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
		t.join();
	}
}
