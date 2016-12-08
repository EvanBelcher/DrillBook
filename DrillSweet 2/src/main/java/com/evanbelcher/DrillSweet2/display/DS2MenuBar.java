package com.evanbelcher.DrillSweet2.display;

import com.evanbelcher.DrillSweet2.*;
import com.evanbelcher.DrillSweet2.data.*;
import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
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

		menuItem = new JMenuItem("Help");
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("help");
		menuItem.addActionListener(this);
		add(menuItem);

		menuItem = new JMenuItem("About");
		menuItem.setMaximumSize(new Dimension(menuItem.getPreferredSize().width, Integer.MAX_VALUE));
		menuItem.setActionCommand("about");
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

	private void help() {
		//String msg = "<html><h1><a id=\"Usage_0\"></a>Usage</h1><br>" + "<h2><a id=\"Installation_2\"></a>Installation</h2><br>" + "<ul><br>" + "<li>Just go to the Releases section and download the most recent EXE.</li><br>" + "</ul><br>" + "<h2><a id=\"Terminology_5\"></a>Terminology</h2><br>" + "<ul><br>" + "<li>Page<br>" + "<ul><br>" + "<li>Individual sheet of drill paper. Also known as a set. A page has <a href=\"../master/Usage.md#editing-the-page\">a set of information</a> associated with it.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Show<br>" + "<ul><br>" + "<li>Set of pages. An entire show is stored in one DS2 file <strong>which can be opened as a JSON file</strong>.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Dot<br>" + "<ul><br>" + "<li>Individual person represented on a page. A dot has a <a href=\"../master/Usage.md#editing-dots\">a set of information</a> associated with it.</li><br>" + "</ul><br>" + "</li><br>" + "<li>DrillSweet directory<br>" + "<ul><br>" + "<li><strong>Documents/DrillSweet 2/</strong> by default</li><br>" + "</ul><br>" + "</li><br>" + "</ul><br>" + "<h2><a id=\"Placing_and_moving_dots_15\"></a>Placing and moving dots</h2><br>" + "<ul><br>" + "<li>Click in the bounds of the field to place a dot. It will be automatically selected.</li><br>" + "<li>Click on an existing dot to select it.</li><br>" + "<li>Click and drag a dot to move it.<br>" + "<ul><br>" + "<li>When clicking and dragging, right-click to cancel the move.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Right-click a dot to delete it.</li><br>" + "<li>Right click any empty space to deselect the currently selected dot.</li><br>" + "</ul><br>" + "<h2><a id=\"Editing_dots_23\"></a>Editing dots</h2><br>" + "<p>The “Dot Data” pane on the right side of the screen contains controls for the selected dot.<br><br>" + "Controls:</p><br>" + "<ul><br>" + "<li>Instrument<br>" + "<ul><br>" + "<li>Select a letter from A-Z. This will be displayed above the dot on the page.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Number<br>" + "<ul><br>" + "<li>Select any number 1 - infinity. This will be displayed above the dot on the page.</li><br>" + "</ul><br>" + "</li><br>" + "<li>X Position<br>" + "<ul><br>" + "<li>Changed by clicking and dragging point as explained <a href=\"../master/Usage.md#placing-and-moving-dots\">above</a>. Also editable with the controls.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Y Position<br>" + "<ul><br>" + "<li>Changed by clicking and dragging point as explained <a href=\"../master/Usage.md#placing-and-moving-dots\">above</a>. Also editable with the controls.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Position Text<br>" + "<ul><br>" + "<li>Not editable. Displays the position of the dot in English.</li><br>" + "</ul><br>" + "</li><br>" + "</ul><br>" + "<h3><a id=\"Notes_37\"></a>Notes:</h3><br>" + "<ul><br>" + "<li>The instrument and number combined make the dot “name”. Duplicate names are allowed technically, although it doesn’t make much sense from a marching standpoint. Telling someone to be in two places at once is a bit evil, even for a band director.</li><br>" + "</ul><br>" + "<h2><a id=\"Editing_the_page_40\"></a>Editing the page</h2><br>" + "<p>The “Page Data” pane on the right side of the screen contains controls for the entire page.</p><br>" + "<ul><br>" + "<li>Navigation<br>" + "<ul><br>" + "<li><br>" + "<p>Pages are shown in the format:</p><br>" + "<blockquote><br>" + "<p>&lt;Page Number&gt; | &lt;Song Title&gt;, m.&lt;Starting Measure&gt;-&lt;Ending Measure&gt;<br><br>" + "- Select a page to display that page. You don’t need to save the page you’re working on before you do this, but it never hurts.</p><br>" + "</blockquote><br>" + "</li><br>" + "</ul><br>" + "</li><br>" + "<li>Page Number<br>" + "<ul><br>" + "<li>Just shows the page number… You didn’t really have a question about this one, right?</li><br>" + "</ul><br>" + "</li><br>" + "<li>Song Title<br>" + "<ul><br>" + "<li>Edit the song title here and watch as it magically updates the navigation box and text box.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Starting Measure<br>" + "<ul><br>" + "<li>^</li><br>" + "</ul><br>" + "</li><br>" + "<li>Ending Measure<br>" + "<ul><br>" + "<li>^</li><br>" + "</ul><br>" + "</li><br>" + "<li>Counts<br>" + "<ul><br>" + "<li>^</li><br>" + "</ul><br>" + "</li><br>" + "<li>Notes<br>" + "<ul><br>" + "<li>Write whatever you want. It will be shown in the text box.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Text X Position<br>" + "<ul><br>" + "<li>Move the text box horizontally.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Text Y Position<br>" + "<ul><br>" + "<li>Move the text box vertically.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Clear Page<br>" + "<ul><br>" + "<li>Remove all dots from the page. <strong>Does not save automatically, so if you somehow managed to do this accidentally, just close without saving and re-open.</strong></li><br>" + "</ul><br>" + "</li><br>" + "<li>Delete Page<br>" + "<ul><br>" + "<li>Remove this page and change the number of other pages to remove the hole. Band directors hate holes. <strong>Does not save automatically, so if you somehow managed to do this accidentally, just close without saving and re-open.</strong></li><br>" + "</ul><br>" + "</li><br>" + "</ul><br>" + "<h2><a id=\"Display_settings_68\"></a>Display settings</h2><br>" + "<ul><br>" + "<li>Toggle Gridlines<br>" + "<ul><br>" + "<li>Click it once and the grid disappears. Click it again and it’s back. Abracadabra!</li><br>" + "</ul><br>" + "</li><br>" + "<li>Toggle Dot Names<br>" + "<ul><br>" + "<li>Clap on. Dot names disappear. Clap off. Dot names re-appear.</li><br>" + "</ul><br>" + "</li><br>" + "<li><strong>Play</strong><br>" + "<ul><br>" + "<li>Plays from the <strong>previous to current</strong> pages. Points that may collide appear in blue. Dots with extremely far moves (&gt;2 steps per count) appear in red.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Undo<br>" + "<ul><br>" + "<li>Remember that thing you just did? Because I don’t.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Redo<br>" + "<ul><br>" + "<li>Oh, it’s back again.</li><br>" + "</ul><br>" + "</li><br>" + "</ul><br>" + "<h2><a id=\"File_menu_80\"></a>File menu</h2><br>" + "<ul><br>" + "<li>New<br>" + "<ul><br>" + "<li>Create a new show in the <a href=\"../master/Usage.md#terminology\">DrillSweet directory</a> by default or wherever you choose. It will ask you for a filename for the new page. You don’t need to give it the “.ds2” extension.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Open<br>" + "<ul><br>" + "<li>Open an existing show in the DrillSweet directory by default or wherever you choose.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Save<br>" + "<ul><br>" + "<li>Save the current show under the current filename. The current filename is displayed in the title bar and taskbar next to “DrillSweet 2”.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Save As<br>" + "<ul><br>" + "<li>Save the current show under a new filename.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Print Current Page<br>" + "<ul><br>" + "<li>Saves the current show as a PDF file in the DrillSweet directory. Takes current “toggle gridlines” and “toggle dot names” states into account.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Print Show<br>" + "<ul><br>" + "<li>Saves every page as a PDF file. The name of the PDF file will be the same as the name of the file of the show, plus “full show”.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Print Dot Sheets<br>" + "<ul><br>" + "<li>Saves dot sheets for every player as PDF files named by their instrument letter. These are all put in a folder called the name of your show “Dot Sheets”. Hope you got all that.</li><br>" + "</ul><br>" + "</li><br>" + "<li>Quit<br>" + "<ul><br>" + "<li>Now why would you want to do that?</li><br>" + "</ul><br>" + "</li><br>" + "</ul><br>" + "<h2><a id=\"Click_controls_98\"></a>Click controls</h2><br>" + "<h3><a id=\"TLDR_100\"></a>TL;DR:</h3><br>" + "<ul><br>" + "<li>Shift-click to draw a rectangle and select all dots in it. Right-click to remove all selected dots.</li><br>" + "<li>Control-click to select/deselect multiple dots one by one. Right-click to remove all selected dots.</li><br>" + "<li>Alt-click to select/deselect all dots by instrument Right click to remove all selected dots.</li><br>" + "<li>Normal click to add or select a dot. Drag to move selected dot(s). Right-click to remove dot.</li><br>" + "</ul><br>" + "<h3><a id=\"Full_Details_106\"></a>Full Details:</h3><br>" + "<h4><a id=\"Holding_shift_108\"></a>Holding shift:</h4><br>" + "<ul><br>" + "<li>Before drag:<br>" + "<ul><br>" + "<li>Right click on selected point to remove all selected points</li><br>" + "<li>Right click on deselected point to do NOTHING</li><br>" + "<li>Right click off point to deselect all</li><br>" + "</ul><br>" + "</li><br>" + "<li>Left-click (on or off point) to drag</li><br>" + "<li>During drag:<br>" + "<ul><br>" + "<li>Draw and show rectangle</li><br>" + "<li>Right click (on or off point) during drag to cancel</li><br>" + "</ul><br>" + "</li><br>" + "<li>Release left click (on or off point) to stop drag and select all in rectangle</li><br>" + "</ul><br>" + "<h4><a id=\"Holding_control_119\"></a>Holding control:</h4><br>" + "<ul><br>" + "<li>Left click on selected point to deselect point</li><br>" + "<li>Left click on deselected point to select point</li><br>" + "<li>Left click off point to do NOTHING</li><br>" + "<li>Right click on selected point to remove all selected points</li><br>" + "<li>Right click on deselected point to deselect all</li><br>" + "<li>Right click off point to deselect all</li><br>" + "</ul><br>" + "<h4><a id=\"Holding_alt_127\"></a>Holding alt:</h4><br>" + "<ul><br>" + "<li>Left click on selected point to deselect all points with same instrument</li><br>" + "<li>Left click on deselected point to select all points with same instrument</li><br>" + "<li>Left click off point to do NOTHING</li><br>" + "<li>Right click on point to delete all points with same instrument</li><br>" + "<li>Right click off point to deselect all</li><br>" + "</ul><br>" + "<h4><a id=\"Normal_134\"></a>Normal:</h4><br>" + "<ul><br>" + "<li>Before drag:<br>" + "<ul><br>" + "<li>Left click off point to add and select point</li><br>" + "<li>Left click on deselected point to select point</li><br>" + "<li>Right click on selected point to deselect and remove all selected points</li><br>" + "<li>Right click on deselected point to remove point</li><br>" + "<li>Right click off point to deselect selected point</li><br>" + "</ul><br>" + "</li><br>" + "<li>Left click on selected point to drag</li><br>" + "<li>During drag:<br>" + "<ul><br>" + "<li>Move and show selected point(s)</li><br>" + "<li>Right click on or off point during drag to cancel</li><br>" + "</ul><br>" + "</li><br>" + "<li>Release left click (on or off point) to stop drag and deposit selected point(s)</li><br>" + "</ul><br>" + "<br>" + "</body></html>";
		String msg = null;

		try {
			msg = IOUtils.toString(Main.getFile("Usage.md", this));
			msg = new PegDownProcessor().markdownToHtml(msg);
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
		scrollPane.setPreferredSize(new Dimension(GraphicsRunner.SCREEN_SIZE.width - 100, GraphicsRunner.SCREEN_SIZE.height - 100));
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
			str += "\nTo play from the first page to the second page, navigate to the second page and click \"Play\".";
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
			str += "\nTo play from the first page to the second page, navigate to the second page and click \"Play\".";
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

	/**
	 * Disables all menu items
	 */
	public void disableAll() {
		for (Component component : getComponents()) {
			if (!(component instanceof JMenuItem) || (!((JMenuItem) component).getText().equals("Toggle Gridlines") && !((JMenuItem) component).getText().equals("Toggle Dot Names")))
				component.setEnabled(false);
		}
	}

	/**
	 * Enables all menu items
	 */
	public void enableAll() {
		for (Component component : getComponents())
			component.setEnabled(true);
	}
}
