package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.*;
import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.*;
import net.miginfocom.swing.MigLayout;

/**
 * The JInternalFrame holding controls to change the page
 * 
 * @author Evan Belcher
 * @version 1.0
 * @since 1.0
 */
public class PageDataFrame extends JInternalFrame {
	
	private static final long serialVersionUID = 377622521569426205L;
	
	private JComboBox<String> navigation;
	private JLabel number;
	private JTextField song;
	private JSpinner startingMeasure;
	private JSpinner endingMeasure;
	private JSpinner counts;
	private JTextArea notes;
	private JButton delete;
	private JButton clear;
	private JSpinner textX;
	private JSpinner textY;
	
	private Page currentPage;
	private static int padding = 50;
	private static boolean deleting = false;
	
	/**
	 * Creates the PageDataFrame object, initializes and adds components
	 * 
	 * @since 1.0
	 */
	public PageDataFrame() {
		super("Page Data", false, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable
		
		getCurrentPage();
		
		navigation = getNavigation();
		number = getNumber();
		song = getSong();
		startingMeasure = getStartingMeasure();
		endingMeasure = getEndingMeasure();
		counts = getCounts();
		notes = getNotes();
		textX = getTextX();
		textY = getTextY();
		clear = new JButton("Clear Page");
		clear.addActionListener((ActionEvent e) -> {
			int i = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear this page?", "Clear Page", JOptionPane.YES_NO_OPTION);
			if (i == 0) {
				clearPage();
			}
		});
		delete = new JButton("Delete Page");
		delete.addActionListener((ActionEvent e) -> {
			int i = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this page?", "Delete Page", JOptionPane.YES_NO_OPTION);
			if (i == 0) {
				deletePage();
			}
		});
		
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
		add(notes, "span 2 2");
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
		setLocation(GraphicsRunner.SCREEN_SIZE.width - getSize().width - padding, padding);
	}
	
	/**
	 * Sets the currentPage field to equal the current page and returns it
	 * 
	 * @return the current page
	 * @since 1.0
	 */
	public Page getCurrentPage() {
		currentPage = Main.getCurrentPage();
		return currentPage;
	}
	
	/**
	 * Initializes navigation to include the display strings of all of the pages, and New Page
	 * 
	 * @return navigation
	 * @since 1.0
	 */
	private JComboBox<String> getNavigation() {
		ConcurrentHashMap<Integer, Page> pages = Main.getPages();
		String[] vals = new String[pages.size() + 1];
		for (int i = 1; i <= pages.size(); i++) {
			vals[pages.size() - i] = pages.get(i).toDisplayString();
		}
		vals[pages.size()] = "New Page";
		JComboBox<String> nav = new JComboBox<String>(vals);
		nav.setSelectedIndex(nav.getItemCount() - Main.getState().getCurrentPage() - 1);
		nav.addItemListener(new NavigationItemListener(this, nav, currentPage));
		return nav;
	}
	
	/**
	 * Updates navigation's text to be current
	 * 
	 * @since 1.0
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
	 * @since 1.0
	 */
	private JLabel getNumber() {
		JLabel number = new JLabel(String.valueOf(currentPage.getNumber()));
		return number;
	}
	
	/**
	 * Initializes song to be the current page's song
	 * 
	 * @return song
	 * @since 1.0
	 */
	private JTextField getSong() {
		JTextField song = new JTextField(50);
		song.setText(currentPage.getSong());
		song.setEditable(true);
		song.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
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
	 * @since 1.0
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
	 * @since 1.0
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
	 * @since 1.0
	 */
	private JSpinner getCounts() {
		JSpinner counts = new JSpinner(new SpinnerNumberModel(currentPage.getCounts(), 1, Integer.MAX_VALUE, 1));
		((DefaultEditor) counts.getEditor()).getTextField().setEditable(true);
		counts.addChangeListener((ChangeEvent e) -> {
			currentPage.setCounts((int) counts.getValue());
		});
		return counts;
	}
	
	/**
	 * Initializes notes to be the current page's notes
	 * 
	 * @return notes
	 * @since 1.0
	 */
	private JTextArea getNotes() {
		JTextArea notes = new JTextArea(10, 20);
		notes.setText(currentPage.getNotes());
		notes.setEditable(true);
		notes.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				currentPage.setNotes(notes.getText());
			}
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				currentPage.setNotes(notes.getText());
			}
			
		});
		return notes;
	}
	
	/**
	 * Initializes textX to be the current page's text x position
	 * 
	 * @return textX
	 * @since 1.0
	 */
	private JSpinner getTextX() {
		JSpinner textX = new JSpinner(new SpinnerNumberModel(Main.getCurrentPage().getTextPoint().x, 0, Integer.MAX_VALUE, 5));
		((DefaultEditor) textX.getEditor()).getTextField().setEditable(true);
		textX.addChangeListener((ChangeEvent e) -> {
			Main.getCurrentPage().setTextPoint(new Point((int) textX.getValue(), Main.getCurrentPage().getTextPoint().y));
		});
		return textX;
	}
	
	/**
	 * Initializes textY to be the current page's text y position
	 * 
	 * @return textY
	 * @since 1.0
	 */
	private JSpinner getTextY() {
		JSpinner textY = new JSpinner(new SpinnerNumberModel(Main.getCurrentPage().getTextPoint().y, 0, Integer.MAX_VALUE, 5));
		((DefaultEditor) textY.getEditor()).getTextField().setEditable(true);
		textY.addChangeListener((ChangeEvent e) -> {
			Main.getCurrentPage().setTextPoint(new Point(Main.getCurrentPage().getTextPoint().x, (int) textY.getValue()));
		});
		return textY;
	}
	
	/**
	 * Clears the current page
	 * 
	 * @since 1.0
	 */
	private void clearPage() {
		deleting = true;
		Main.getCurrentPage().getDots().clear();
		updateAll();
		deleting = false;
	}
	
	/**
	 * Deletes the current page
	 * 
	 * @since 1.0
	 */
	public void deletePage() {
		if (Main.getPages().size() == 1) {
			clearPage();
			return;
		}
		deleting = true;
		navigation.removeItemListener(navigation.getItemListeners()[0]);
		
		int num = currentPage.getNumber();
		ConcurrentHashMap<Integer, Page> oldPages = new ConcurrentHashMap<>(Main.getPages());
		for (int i : Main.getPages().keySet()) {
			if (i > num) {
				Page p = oldPages.get(i);
				p.setNumber(i - 1);
				Main.getRealPages().put(i - 1, p);
			}
		}
		Main.getRealPages().remove(Main.getPages().size());
		
		getCurrentPage();
		ConcurrentHashMap<Integer, Page> pages = Main.getPages();
		String[] vals = new String[pages.size() + 1];
		for (int i = 1; i <= pages.size(); i++) {
			vals[pages.size() - i] = pages.get(i).toDisplayString();
		}
		vals[pages.size()] = "New Page";
		navigation.removeAllItems();
		for (int i = 0; i < vals.length; i++)
			navigation.addItem(vals[i]);
		navigation.setSelectedIndex(navigation.getItemCount() - Main.getState().getCurrentPage() - 1);
		
		navigation.addItemListener(new NavigationItemListener(this, navigation, currentPage));
		updateAll();
		deleting = false;
	}
	
	/**
	 * Updates all of the components
	 * 
	 * @since 1.0
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
	 * @return deleting
	 * @since 1.0
	 */
	public static boolean getDeleting() {
		return deleting;
	}
	
}
