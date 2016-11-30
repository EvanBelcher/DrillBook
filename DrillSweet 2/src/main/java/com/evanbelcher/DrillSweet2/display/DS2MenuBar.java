package com.evanbelcher.DrillSweet2.display;

import com.evanbelcher.DrillSweet2.*;
import com.evanbelcher.DrillSweet2.data.*;
import com.evanbelcher.DrillSweet2.display.play.PagePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
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
		menu.setMnemonic(KeyEvent.VK_D);
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

		//add these to the menubar itself
		menuItem = new JMenuItem("Toggle Gridlines");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("togglegrid");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("Toggle Dot Names");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("togglenames");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("Play");
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("play");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("Undo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("undo");
		menuItem.addActionListener(this);
		add(menuItem);
		menuItem = new JMenuItem("Redo");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("redo");
		menuItem.addActionListener(this);
		add(menuItem);
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
				try {
					openShow();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
				Main.getState().setShowGrid(!Main.getState().isShowGrid());
				break;
			case "togglenames":
				Main.getState().setShowNames(!Main.getState().isShowNames());
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
			case "quit":
			default:
				gr.dispatchEvent(new WindowEvent(gr, WindowEvent.WINDOW_CLOSING));
		}
		desktop.getIO().fixControl();
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
	 *
	 * @throws InterruptedException if there is an error when waiting for the saves to finish
	 */
	private void openShow() throws InterruptedException {
		if (askToSave()) {

			File file;
			final JFileChooser fc = new JFileChooser(Main.getFilePath());
			fc.setFileFilter(new DS2FileFilter());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal;
			do {
				returnVal = fc.showOpenDialog(this);
				file = fc.getSelectedFile();
			} while (!file.exists());

			String name, path;

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
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

	private void play() {
		if (checkNoDuplicates() && checkNoMissing()) {
			PagePlayer p = new PagePlayer(Main.getPages().get(Main.getState().getCurrentPage() - 1).getDots(), Main.getCurrentPage().getDots(), Main.getCurrentPage().getCounts(), desktop);
			new Thread(p, "playerThread").start();
		}
	}

	private boolean checkNoDuplicates() {
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> badNames = new ArrayList<>();
		DS2ConcurrentHashMap<Point, String> dots = Main.getCurrentPage().getDots();
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
			JOptionPane.showMessageDialog(this, str.trim(), "Conflicts!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return checkNoDuplicates(Main.getState().getCurrentPage() - 1);
	}

	private boolean checkNoDuplicates(int pageNum) {
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> badNames = new ArrayList<>();
		DS2ConcurrentHashMap<Point, String> dots;
		try {
			dots = Main.getPages().get(pageNum).getDots();
		} catch (NullPointerException e) {
			if (pageNum == 0)
				JOptionPane.showMessageDialog(this, "To play from the first page to the second page, navigate to the second page and click \"Play\".", "Can't play the first page", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(this, str.trim(), "Conflicts!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private boolean checkNoMissing() {
		int currentPageNum = Main.getState().getCurrentPage();
		if (currentPageNum == 1) {
			JOptionPane.showMessageDialog(this, "To play from the first page to the second page, navigate to the second page and click \"Play\".", "Can't play the first page", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		DS2ConcurrentHashMap<Point, String> currentDots = Main.getCurrentPage().getDots();
		DS2ConcurrentHashMap<Point, String> previousDots = Main.getPages().get(currentPageNum - 1).getDots();
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
}
