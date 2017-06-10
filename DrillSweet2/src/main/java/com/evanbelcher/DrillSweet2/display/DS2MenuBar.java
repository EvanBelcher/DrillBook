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

import com.evanbelcher.DrillSweet2.*;
import com.evanbelcher.DrillSweet2.data.*;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Custom JMenuBar holding the miscellaneous controls
 *
 * @author Evan Belcher
 */
public class DS2MenuBar extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = -48484928431063770L;
	private GraphicsRunner gr;
	private DS2DesktopPane desktop;

	/**
	 * Constructs DS2MenuBar. Adds the menu and menuitems.
	 *
	 * @param graphicsRunner the JFrame that created this
	 * @param desktop        the DS2DesktopPane in the JFrame
	 */
	public DS2MenuBar(GraphicsRunner graphicsRunner, DS2DesktopPane desktop) {
		super();

		gr = graphicsRunner;
		this.desktop = desktop;

		//Set up the menu
		JMenu menu = new JMenu("File");
		add(menu);

		//Set up the menu items.
		JMenuItem menuItem = new JMenuItem("New");
		menuItem.setMnemonic(KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("new");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Open");
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("open");
		menuItem.addActionListener(this);
		menuItem.setLayout(new MigLayout());
		menu.add(menuItem);

		menuItem = new JMenuItem("Save");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("save");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save As");
		menuItem.setActionCommand("saveas");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Print	 Current Page");
		menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("printpage");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Print Show");
		menuItem.setActionCommand("printshow");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Print Dot Sheets");
		menuItem.setActionCommand("printdotsheets");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("quit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu = new JMenu("Edit");
		add(menu);

		menuItem = new JMenuItem("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("undo");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Redo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("redo");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu = new JMenu("Settings");
		add(menu);

		menuItem = new JMenuItem("Toggle Gridlines");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("togglegrid");
		menuItem.addActionListener(this);
		menuItem.setForeground(Main.getState().getSettings().shouldShowGrid() ? Color.BLACK : Color.RED);
		menu.add(menuItem);

		menuItem = new JMenuItem("Toggle Dot Names");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
		menuItem.setActionCommand("togglenames");
		menuItem.addActionListener(this);
		menuItem.setForeground(Main.getState().getSettings().shouldShowNames() ? Color.BLACK : Color.RED);
		menu.add(menuItem);

		menuItem = new JMenuItem("Toggle Text Box");
		menuItem.setActionCommand("toggletext");
		menuItem.addActionListener(this);
		menuItem.setForeground(Main.getState().getSettings().shouldShowText() ? Color.BLACK : Color.RED);
		menu.add(menuItem);

		menuItem = new JMenuItem("Color Code Dots by Instrument");
		menuItem.setActionCommand("colordots");
		menuItem.addActionListener(this);
		menuItem.setForeground(Main.getState().getSettings().shouldColorDots() ? Color.BLACK : Color.RED);
		menu.add(menuItem);

		menuItem = new JMenuItem();
		menuItem.setText(Main.getState().getSettings().useCollegeHashes() ? "Change to High School Hashes" : "Change to College Hashes");
		menuItem.setActionCommand("changehash");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Change Font Size");
		menuItem.setActionCommand("fontsize");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		//add(Box.createHorizontalStrut(menu.getPreferredSize().width));

		//add these to the menubar itself
		menuItem = new JMenuItem("Play");
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("play");
		menuItem.addActionListener(this);
		add(menuItem);

		add(Box.createHorizontalGlue());

		menu = new JMenu("Help");
		add(menu);

		menuItem = new JMenuItem("Help");
		menuItem.setActionCommand("help");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("About");
		menuItem.setActionCommand("about");
		menuItem.addActionListener(this);
		menu.add(menuItem);
	}

	/**
	 * Makes the given file name valid for a Windows operating system.
	 *
	 * @param filename the file name to be cleansed
	 * @return the cleansed file name
	 */
	public static String cleanseFileName(String filename) {
		filename = filename.trim();
		filename = filename.replaceAll("[<>:\"/\\\\|?*]", "");
		filename = filename.trim();
		if (!filename.isEmpty() && filename.charAt(filename.length() - 1) == '.')
			filename = filename.substring(0, filename.length() - 1);
		filename = filename.trim();
		ArrayList<String> arr = new ArrayList<>(Arrays.asList(new String[] { "CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9" }));
		if (filename.isEmpty() || arr.contains(filename))
			filename = "newfile";
		filename = filename.trim();
		return filename;
	}

	/**
	 * On any menu item click.
	 */
	@Override public void actionPerformed(ActionEvent arg0) {
		State.print(arg0.getActionCommand());
		switch (arg0.getActionCommand()) {
			case "new": //Try to save work, open new show
				try {
					newShow();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "open": //Try to save work, get new show
				openShow();
				break;
			case "save":
				Main.save();
				break;
			case "saveas":
				try {
					saveAs();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "printpage":
				try {
					desktop.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					desktop.printCurrentPageToPdf();
					desktop.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "printshow":
				try {
					desktop.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					desktop.printAllPagesToPdf();
					desktop.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "printdotsheets":
				try {
					desktop.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					new DotSheetMaker().printDotSheets();
					desktop.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "togglegrid":
				Main.getState().getSettings().setShowGrid(!Main.getState().getSettings().shouldShowGrid());
				((JMenu) getComponent(2)).getMenuComponent(0).setForeground(Main.getState().getSettings().shouldShowGrid() ? Color.BLACK : Color.RED);
				break;
			case "togglenames":
				Main.getState().getSettings().setShowNames(!Main.getState().getSettings().shouldShowNames());
				((JMenu) getComponent(2)).getMenuComponent(1).setForeground(Main.getState().getSettings().shouldShowNames() ? Color.BLACK : Color.RED);
				break;
			case "toggletext":
				Main.getState().getSettings().setShowText(!Main.getState().getSettings().shouldShowText());
				((JMenu) getComponent(2)).getMenuComponent(2).setForeground(Main.getState().getSettings().shouldShowText() ? Color.BLACK : Color.RED);
				break;
			case "colordots":
				Main.getState().getSettings().setColorDots(!Main.getState().getSettings().shouldColorDots());
				((JMenu) getComponent(2)).getMenuComponent(3).setForeground(Main.getState().getSettings().shouldColorDots() ? Color.BLACK : Color.RED);
				break;
			case "changehash":
				Main.getState().getSettings().setCollegeHashes(!Main.getState().getSettings().useCollegeHashes());
				((JMenuItem) (((JMenu) getComponent(2)).getMenuComponent(4))).setText(Main.getState().getSettings().useCollegeHashes() ? "Change to High School Hashes" : "Change to College Hashes");
				try {
					desktop.getImage();
				} catch (IOException e) {
					e.printStackTrace();
				}
				desktop.getDotDataFrame().updatePosition();
				break;
			case "fontsize":
				changeFontSize();
				break;
			case "play":
				play();
				break;
			case "undo":
				Main.getState().undo();
				desktop.getIO().clearActivePoints();
				desktop.getDotDataFrame().updateAll(desktop.getActivePoints());
				break;
			case "redo":
				Main.getState().redo();
				desktop.getIO().clearActivePoints();
				desktop.getDotDataFrame().updateAll(desktop.getActivePoints());
				break;
			case "help":
				help();
				break;
			case "about":
				about();
				break;
			case "quit":
			default:
				gr.dispatchEvent(new WindowEvent(gr, WindowEvent.WINDOW_CLOSING));
		}
		desktop.getIO().fixControl();
	}

	/**
	 * Changes the font size to what the user selects (between 8-30)
	 */
	private void changeFontSize() {
		Integer[] nums = new Integer[23];
		for (int i = 8; i <= 30; i++) {
			nums[i - 8] = i;
		}
		try {
			int size = (int) JOptionPane.showInputDialog(this, "Choose a font size:", "Font Size", JOptionPane.PLAIN_MESSAGE, null, nums, Main.getState().getSettings().getFontSize());
			FontUIResource font = new FontUIResource("Dialog", Font.BOLD, size);
			Main.setUIFont(font);
			Main.getState().getSettings().setFontSize(size);
			SwingUtilities.updateComponentTreeUI(gr);

			for (JInternalFrame i : desktop.getAllFrames())
				i.pack();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays about window.
	 */
	private void about() {
		String msg = "<html>DrillSweet 2 " + State.VERSION + "<br>Created by Evan Belcher, 2016<br><a href=\"https://github.com/EbMinor3/DrillSweet2\">GitHub</a><br><a href=\"http://evanbelcher.com\">Website</a><br><br><a href=\"https://icons8.com\">Icon pack by Icons8</a></html>";
		JEditorPane editorPane = new JEditorPane("text/html", msg);
		editorPane.setEditable(false);
		editorPane.setBackground(new JLabel().getBackground());
		editorPane.addHyperlinkListener(e -> {
			if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
				try {
					Desktop.getDesktop().browse(e.getURL().toURI());
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});

		if (!(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))) {
			editorPane.setContentType("text");
			editorPane.setText("DrillSweet 2 " + State.VERSION + "\nCreated by Evan Belcher, 2016\nGitHub: https://github.com/EbMinor3/DrillSweet2\nWebsite: http://evanbelcher.com\n\nIcon pack by Icons8: https://icons8.com");
		}

		JOptionPane.showMessageDialog(this, editorPane, "About", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Displays help window
	 */
	private void help() {
		String msg = "";

		MutableDataSet options = new MutableDataSet();
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();

		try {
			msg = IOUtils.toString(Main.getFile("Usage.md", this));
			Node document = parser.parse(msg);
			msg = renderer.render(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JTextPane area = new JTextPane();
		area.setContentType("text/html");
		area.setText(msg);
		area.setCaretPosition(0);
		area.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setMaximumSize(new Dimension(GraphicsRunner.SCREEN_SIZE.width, GraphicsRunner.SCREEN_SIZE.height));
		scrollPane.setPreferredSize(new Dimension(GraphicsRunner.SCREEN_SIZE.width - 10, GraphicsRunner.SCREEN_SIZE.height - 10));
		scrollPane.scrollRectToVisible(new Rectangle());
		JOptionPane.showMessageDialog(this, scrollPane, "Help", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Prompts the user if they want to save.
	 *
	 * @return false if they want to cancel
	 */
	private boolean askToSave() {
		int i = JOptionPane.showConfirmDialog(this, "Would you like to save your work first?", "Unsaved Work", JOptionPane.YES_NO_CANCEL_OPTION);
		if (i == 2)
			return false;
		if (i == 0) {
			try {
				Main.savePages().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * Creates a new json file for a new show
	 *
	 * @throws InterruptedException if there is an error when waiting for the saves to finish
	 */
	private void newShow() throws InterruptedException {
		if (askToSave()) {
			final JFileChooser fc = new JFileChooser(new File(Main.getFilePath()));
			fc.setFileFilter(new DS2FileFilter());
			int returnVal = fc.showDialog(this, "New File");

			String name, path;

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					File file = fc.getSelectedFile();
					path = file.getCanonicalPath();
					path = path.substring(0, path.lastIndexOf('\\') + 1);
					name = file.getName().toLowerCase().endsWith(".ds2") ? file.getName() : file.getName() + ".ds2";
					Main.setFilePath(path);
					Main.setPagesFileName(name);
					Main.saveState().join();
					Main.load(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the show to open and opens the respective json file.
	 */
	private void openShow() {
		if (askToSave()) {

			File file;
			final JFileChooser fc = new JFileChooser(Main.getFilePath());
			fc.setFileFilter(new DS2FileFilter());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal;
			do {
				returnVal = fc.showOpenDialog(this);
				file = fc.getSelectedFile();
			} while (returnVal != JFileChooser.CANCEL_OPTION && returnVal != JFileChooser.ERROR_OPTION && !file.exists());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					String name, path;
					path = file.getCanonicalPath();
					path = path.substring(0, path.lastIndexOf('\\') + 1);
					name = file.getName().toLowerCase().endsWith(".ds2") ? file.getName() : file.getName() + ".ds2";
					Main.setFilePath(path);
					Main.setPagesFileName(name);
					Main.load(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Saves the current file under a new name/path
	 *
	 * @throws InterruptedException if there is an error when waiting for the saves to finish
	 */
	private void saveAs() throws InterruptedException {
		if (askToSave()) {

			final JFileChooser fc = new JFileChooser(Main.getFilePath());
			fc.setFileFilter(new DS2FileFilter());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fc.showSaveDialog(this);
			File file = fc.getSelectedFile();

			String name, path;

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					path = file.getCanonicalPath();
					path = path.substring(0, path.lastIndexOf('\\') + 1);
					name = file.getName().toLowerCase().endsWith(".ds2") ? file.getName() : file.getName() + ".ds2";
					Main.setFilePath(path);
					Main.setPagesFileName(name);
					Main.savePages().join();
					Main.saveState().join();
					Main.load(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Opens the player
	 */
	private void play() {
		if (checkNoDuplicates() && checkNoMissing()) {
			gr.toPlayMode(desktop);
		}
	}

	/**
	 * Checks to make sure that the current page has no duplicate dots
	 */
	private boolean checkNoDuplicates() {
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> badNames = new ArrayList<>();
		PointConcurrentHashMap<Point, String> dots = Main.getCurrentPage().getDots();
		for (String s : dots.values()) {
			if (!names.contains(s))
				names.add(s);
			else
				badNames.add(s);
		}
		if (!badNames.isEmpty()) {
			String str = "The following players have more than one dot on the page:\n";
			for (String s : badNames)
				str += s + "\n";
			str += "\nTo data from the first page to the second page, navigate to the second page and click \"Play\".";
			JOptionPane.showMessageDialog(this, str.trim(), "Conflicts!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return checkNoDuplicates(Main.getState().getCurrentPage() - 1);
	}

	/**
	 * Checks to make sure that the given page has no duplciate dots
	 *
	 * @param pageNum the page number to check
	 */
	private boolean checkNoDuplicates(int pageNum) {
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> badNames = new ArrayList<>();
		PointConcurrentHashMap<Point, String> dots;
		try {
			dots = Main.getPages().get(pageNum).getDots();
		} catch (NullPointerException e) {
			if (pageNum == 0)
				JOptionPane.showMessageDialog(this, "To data from the first page to the second page, navigate to the second page and click \"Play\".", "Can't data the first page", JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(this, "Cannot find previous page", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		for (String s : dots.values()) {
			if (!names.contains(s))
				names.add(s);
			else
				badNames.add(s);
		}
		if (!badNames.isEmpty()) {
			String str = "The following players have more than one dot on Page " + pageNum + ":\n";
			for (String s : badNames)
				str += s + "\n";
			str += "\nTo data from the first page to the second page, navigate to the second page and click \"Play\".";
			JOptionPane.showMessageDialog(this, str.trim(), "Conflicts!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Check to make sure there are no points in the first page that aren't in the second page
	 */
	private boolean checkNoMissing() {
		int currentPageNum = Main.getState().getCurrentPage();
		if (currentPageNum == 1) {
			JOptionPane.showMessageDialog(this, "To data from the first page to the second page, navigate to the second page and click \"Play\".", "Can't data the first page", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		PointConcurrentHashMap<Point, String> currentDots = Main.getCurrentPage().getDots();
		PointConcurrentHashMap<Point, String> previousDots = Main.getPages().get(currentPageNum - 1).getDots();
		ArrayList<String> badNames = new ArrayList<>();
		for (String name : previousDots.values()) {
			if (!currentDots.containsValue(name))
				badNames.add(name);
		}
		if (!badNames.isEmpty()) {
			String str = "The following players from last page have disappeared on this page:\n";
			for (String s : badNames)
				str += s + "\n";
			JOptionPane.showMessageDialog(this, str.trim(), "Missing Players!", JOptionPane.INFORMATION_MESSAGE);
		}
		return true;
	}

	/**
	 * Disables most menu items
	 */
	public void disableMost() {
		//		for (Component component : getComponents()) {
		//			if (component instanceof JMenu && ((JMenu) component).getText().equals("Settings")) {
		//				for (Component menuComponent : ((JMenu) component).getMenuComponents()) {
		//					if (!(menuComponent instanceof JMenuItem) || (!((JMenuItem) menuComponent).getText().equals("Toggle Gridlines") && !((JMenuItem) menuComponent).getText().equals("Toggle Dot Names")))
		//						menuComponent.setEnabled(false);
		//				}
		//			} else {
		//				component.setEnabled(false);
		//			}
		//		}
		for (Component component : getComponents()) {
			if (component instanceof JMenu) {
				boolean enabled = false;
				for (Component menuComponent : ((JMenu) component).getMenuComponents()) {
					JMenuItem menuItem = (JMenuItem) menuComponent;
					switch (menuItem.getText()) {
						case "Quit":
						case "Toggle Gridlines":
						case "Toggle Dot Names":
						case "Help":
						case "About":
							enabled = true;
							break;
						default:
							menuItem.setEnabled(false);
					}
				}
				if (!enabled)
					component.setEnabled(false);
			} else
				component.setEnabled(false);
		}
	}

	/**
	 * Enables all menu items
	 */
	public void enableAll() {
		for (Component component : getComponents()) {
			component.setEnabled(true);
			if (component instanceof JMenu) {
				for (Component menuComponent : ((JMenu) component).getMenuComponents())
					menuComponent.setEnabled(true);
			}
		}
	}
}
