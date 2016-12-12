

package com.evanbelcher.DrillSweet2.display;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.data.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The JInternalFrame holding controls to change the page
 *
 * @author Evan Belcher
 */
public class PageDataFrame extends JInternalFrame {

	private static final long serialVersionUID = 377622521569426205L;
	private static boolean deleting = false;
	private JComboBox<String> navigation;
	private JLabel number;
	private JTextField song;
	private JSpinner startingMeasure;
	private JSpinner endingMeasure;
	private JSpinner counts;
	private JTextArea notes;
	private JSpinner textX;
	private JSpinner textY;
	private Page currentPage;

	/**
	 * Creates the PageDataFrame object, initializes and adds components
	 */
	public PageDataFrame() {
		super("Page Data", false, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable
		setFrameIcon(null);

		getCurrentPage();

		//set up components
		navigation = getNavigation();
		number = getNumber();
		song = getSong();
		startingMeasure = getStartingMeasure();
		endingMeasure = getEndingMeasure();
		counts = getCounts();
		notes = getNotes();
		textX = getTextX();
		textY = getTextY();
		JButton clear = getClear();
		JButton delete = getDelete();

		//add components to layout
		setLayout(new MigLayout("wrap 2"));
		add(new JLabel("Navigation:"));
		add(navigation, "span 2");
		add(new JLabel("Page Number:"));
		add(number);
		add(new JLabel("Song Title:"));
		add(song, "span 2");
		add(new JLabel("Starting Measure:"));
		add(startingMeasure);
		add(new JLabel("Ending Measure:"));
		add(endingMeasure);
		add(new JLabel("Counts:"));
		add(counts);
		add(new JLabel("Notes:"));
		add(new JScrollPane(notes), "span 2 2");
		add(new JLabel());
		add(new JLabel("Text X Position"));
		add(textX);
		add(new JLabel("Text Y Position"));
		add(textY);
		add(clear);
		add(delete);

		//...Then set the window size or call pack...
		pack();

		//Set the window's location.
		setLocation(GraphicsRunner.SCREEN_SIZE.width - getSize().width, 0);
	}

	/**
	 * Returns whether a page is deleting
	 */
	public static boolean getDeleting() {
		return deleting;
	}

	/**
	 * Sets the currentPage field to equal the current page and returns it
	 *
	 * @return the current page
	 */
	public Page getCurrentPage() {
		currentPage = Main.getCurrentPage();
		return currentPage;
	}

	/**
	 * Initializes navigation to include the display strings of all of the pages, and New Page
	 *
	 * @return navigation
	 */
	private JComboBox<String> getNavigation() {
		ConcurrentHashMap<Integer, Page> pages = Main.getPages();

		//get each value in pages. sort in descending order, with "New Page" at the bottom
		String[] vals = new String[pages.size() + 1];
		for (int i = 1; i <= pages.size(); i++) {
			vals[pages.size() - i] = pages.get(i).toDisplayString();
		}
		vals[pages.size()] = "New Page";
		JComboBox<String> nav = new JComboBox<>(vals);

		//Set the selected index to the current page
		nav.setSelectedIndex(nav.getItemCount() - Main.getState().getCurrentPage() - 1);
		nav.addItemListener(new NavigationItemListener(this, nav, currentPage));
		return nav;
	}

	/**
	 * Updates navigation's text to be current
	 */
	private void updateNavigation() {
		navigation.removeItemListener(navigation.getItemListeners()[0]);

		int index = navigation.getSelectedIndex();
		navigation.removeItem(navigation.getSelectedItem());
		navigation.insertItemAt(currentPage.toDisplayString(), index);
		navigation.setSelectedIndex(index);

		navigation.addItemListener(new NavigationItemListener(this, navigation, currentPage));
	}

	/**
	 * Initializes number to the number of the current page
	 *
	 * @return number
	 */
	private JLabel getNumber() {
		return new JLabel(String.valueOf(currentPage.getNumber()));
	}

	/**
	 * Initializes song to be the current page's song
	 *
	 * @return song
	 */
	private JTextField getSong() {
		JTextField song = new JTextField(50);
		song.setText(currentPage.getSong());
		song.setEditable(true);

		//update the navigation with the most recent song
		song.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void changedUpdate(DocumentEvent arg0) {

			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				update();
			}

			@Override public void removeUpdate(DocumentEvent arg0) {
				update();
			}

			private void update() {
				currentPage.setSong(song.getText());
				State.print(currentPage);
				updateNavigation();
				song.setCaretPosition(song.getText().length());
			}

		});
		return song;
	}

	/**
	 * Initializes startingMeasure to be the current page's starting measure [0-infinity)
	 *
	 * @return startingMeasure
	 */
	private JSpinner getStartingMeasure() {
		JSpinner startingMeasure = new JSpinner(new SpinnerNumberModel(currentPage.getStartingMeasure(), 0, Integer.MAX_VALUE, 1));
		((DefaultEditor) startingMeasure.getEditor()).getTextField().setEditable(true);
		startingMeasure.addChangeListener((ChangeEvent e) -> {
			currentPage.setStartingMeasure((int) startingMeasure.getValue());
			updateNavigation();
		});
		return startingMeasure;
	}

	/**
	 * Initializes endingMeasure to be the current page's ending measure [0-infinity)
	 *
	 * @return endingMeasure
	 */
	private JSpinner getEndingMeasure() {
		JSpinner endingMeasure = new JSpinner(new SpinnerNumberModel(currentPage.getEndingMeasure(), 0, Integer.MAX_VALUE, 1));
		((DefaultEditor) endingMeasure.getEditor()).getTextField().setEditable(true);
		endingMeasure.addChangeListener((ChangeEvent e) -> {
			currentPage.setEndingMeasure((int) endingMeasure.getValue());
			updateNavigation();
		});
		return endingMeasure;
	}

	/**
	 * Initializes counts to be the current page's counts [1,infinity)
	 *
	 * @return counts
	 */
	private JSpinner getCounts() {
		JSpinner counts = new JSpinner(new SpinnerNumberModel(currentPage.getCounts(), 1, Integer.MAX_VALUE, 1));
		((DefaultEditor) counts.getEditor()).getTextField().setEditable(true);
		counts.addChangeListener((ChangeEvent e) -> currentPage.setCounts((int) counts.getValue()));
		return counts;
	}

	/**
	 * Initializes notes to be the current page's notes
	 *
	 * @return notes
	 */
	private JTextArea getNotes() {
		JTextArea notes = new JTextArea(10, 50);
		notes.setText(currentPage.getNotes());
		notes.setEditable(true);
		notes.setLineWrap(true);
		notes.setMaximumSize(notes.getPreferredSize());
		notes.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void changedUpdate(DocumentEvent arg0) {
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				currentPage.setNotes(notes.getText());
			}

			@Override public void removeUpdate(DocumentEvent arg0) {
				currentPage.setNotes(notes.getText());
			}

		});
		return notes;
	}

	/**
	 * Initializes textX to be the current page's text x position
	 *
	 * @return textX
	 */
	private JSpinner getTextX() {
		JSpinner textX = new JSpinner(new SpinnerNumberModel(Main.getCurrentPage().getTextPoint().x, 0, Integer.MAX_VALUE, 5));
		((DefaultEditor) textX.getEditor()).getTextField().setEditable(true);
		textX.addChangeListener((ChangeEvent e) -> Main.getCurrentPage().setTextPoint(new Point((int) textX.getValue(), Main.getCurrentPage().getTextPoint().y)));
		return textX;
	}

	/**
	 * Initializes textY to be the current page's text y position
	 *
	 * @return textY
	 */
	private JSpinner getTextY() {
		JSpinner textY = new JSpinner(new SpinnerNumberModel(Main.getCurrentPage().getTextPoint().y, 0, Integer.MAX_VALUE, 5));
		((DefaultEditor) textY.getEditor()).getTextField().setEditable(true);
		textY.addChangeListener((ChangeEvent e) -> Main.getCurrentPage().setTextPoint(new Point(Main.getCurrentPage().getTextPoint().x, (int) textY.getValue())));
		return textY;
	}

	/**
	 * Initializes clear
	 *
	 * @return clear
	 */
	private JButton getClear() {
		JButton clear = new JButton("Clear Page");
		clear.addActionListener((ActionEvent e) -> {
			int i = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear this page?", "Clear Page", JOptionPane.YES_NO_OPTION);
			if (i == 0) {
				clearPage();
			}
		});
		return clear;
	}

	/**
	 * Clears the current page
	 */
	private void clearPage() {
		deleting = true;
		Main.getCurrentPage().getDots().clear();
		updateAll();
		deleting = false;
	}

	/**
	 * Initializes delete
	 *
	 * @return delete
	 */
	private JButton getDelete() {
		JButton delete = new JButton("Delete Page");
		delete.addActionListener((ActionEvent e) -> {
			int i = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this page?", "Delete Page", JOptionPane.YES_NO_OPTION);
			if (i == 0) {
				deletePage();
			}
		});
		return delete;
	}

	/**
	 * Deletes the current page
	 */
	public void deletePage() {
		//if there's only one page, just clear the page instead.
		if (Main.getPages().size() == 1) {
			clearPage();
			return;
		}

		deleting = true;
		navigation.removeItemListener(navigation.getItemListeners()[0]);

		//for each page # in pages, if it's greater than the number of the page we are deleting, add a correctly numbered new page to pages
		int num = currentPage.getNumber();
		ConcurrentHashMap<Integer, Page> oldPages = new ConcurrentHashMap<>(Main.getPages());
		Main.getPages().keySet().stream().filter(i -> i > num).forEachOrdered(i -> {
			Page p = oldPages.get(i);
			p.setNumber(i - 1);
			Main.getRealPages().put(i - 1, p);
		});
		Main.getRealPages().remove(Main.getPages().size());

		getCurrentPage();

		//update navigation
		ConcurrentHashMap<Integer, Page> pages = Main.getPages();
		String[] vals = new String[pages.size() + 1];
		for (int i = 1; i <= pages.size(); i++) {
			vals[pages.size() - i] = pages.get(i).toDisplayString();
		}
		vals[pages.size()] = "New Page";
		navigation.removeAllItems();
		for (String val : vals)
			navigation.addItem(val);
		navigation.setSelectedIndex(navigation.getItemCount() - Main.getState().getCurrentPage() - 1);

		navigation.addItemListener(new NavigationItemListener(this, navigation, currentPage));
		updateAll();
		deleting = false;
	}

	/**
	 * Updates all of the components
	 */
	public void updateAll() {
		getCurrentPage();
		number.setText(String.valueOf(currentPage.getNumber()));
		song.setText(currentPage.getSong());
		startingMeasure.setValue(currentPage.getStartingMeasure());
		endingMeasure.setValue(currentPage.getEndingMeasure());
		counts.setValue(currentPage.getCounts());
		notes.setText(currentPage.getNotes());
		textX.setValue(currentPage.getTextPoint().x);
		textY.setValue(currentPage.getTextPoint().y);
	}

	/**
	 * Updates navigation to the selected page at the end of printing all pages
	 */
	public void updateAfterPrintAll() {
		navigation.removeItemListener(navigation.getItemListeners()[0]);
		getCurrentPage();
		ConcurrentHashMap<Integer, Page> pages = Main.getPages();
		String[] vals = new String[pages.size() + 1];
		for (int i = 1; i <= pages.size(); i++) {
			vals[pages.size() - i] = pages.get(i).toDisplayString();
		}
		vals[pages.size()] = "New Page";
		navigation.removeAllItems();
		for (String val : vals)
			navigation.addItem(val);
		navigation.setSelectedIndex(navigation.getItemCount() - Main.getState().getCurrentPage() - 1);

		navigation.addItemListener(new NavigationItemListener(this, navigation, currentPage));
		updateAll();
	}

}
