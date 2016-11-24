package main.java.com.evanbelcher.DrillSweet2.display;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

import main.java.com.evanbelcher.DrillSweet2.Main;
import main.java.com.evanbelcher.DrillSweet2.data.Page;
import net.miginfocom.swing.MigLayout;

/**
 * Custom JInternal Frame to hold controls for the selected dot;
 *
 * @author Evan Belcher
 */
public class DotDataFrame extends JInternalFrame {

	private static final long serialVersionUID = -5792479023645647921L;

	private JComboBox<String> instrument;
	private JSpinner number;
	private JSpinner xPos;
	private JSpinner yPos;
	private JTextArea position;

	private DS2DesktopPane mdp;
	private static boolean deleting;

	/**
	 * Constructs the object. Adds components.
	 *
	 * @param mdp the DesktopPane that constains this
	 */
	public DotDataFrame(DS2DesktopPane mdp) {
		super("Dot Data", false, //resizable
				false, //closable
				false, //maximizable
				true);//iconifiable

		getCurrentPage();
		this.mdp = mdp;

		//set up components
		instrument = getInstrument();
		number = getNumber();
		xPos = getXPos();
		yPos = getYPos();
		position = getPositionText();

		//add components to layout
		setLayout(new MigLayout("wrap 2"));
		add(new JLabel("Instrument:"));
		add(instrument);
		add(new JLabel("Number:"));
		add(number);
		add(new JLabel("X Position:"));
		add(xPos);
		add(new JLabel("Y Position:"));
		add(yPos);
		add(new JLabel("Position Text:"));
		add(position, "span 2");

		//...Then set the window size or call pack...
		pack();

		setFocusable(false);
	}

	/**
	 * Sets and returns the current page
	 *
	 * @return current page
	 */
	public Page getCurrentPage() {
		return Main.getCurrentPage();
	}

	/**
	 * Initializes instrument to contain the letters A-Z
	 *
	 * @return instrument
	 */
	private JComboBox<String> getInstrument() {
		//populate with letters A-Z
		String[] chars = new String[26];
		for (int i = 0; i < 26; i++)
			chars[i] = String.valueOf((char) (65 + i));
		JComboBox<String> comboBox = new JComboBox<>(chars);

		comboBox.addItemListener((ItemEvent e) -> Main.getCurrentPage().getDots().put(mdp.getActivePoints().get(0), (String) comboBox.getSelectedItem() + (int) number.getValue()));
		comboBox.setFocusable(false);
		if (mdp.getActivePoints().get(0) != null && mdp.getActivePoints().size() == 1) {
			char c = Main.getCurrentPage().getDots().get(mdp.getActivePoints().get(0)).replaceAll("[0-9]", "").charAt(0);
			comboBox.setSelectedIndex(c - 65);
		} else
			comboBox.setEnabled(false);
		return comboBox;
	}

	/**
	 * Initializes number to be the current dot's number
	 *
	 * @return number
	 */
	private JSpinner getNumber() {
		JSpinner spinner;
		if (mdp.getActivePoints().get(0) != null && mdp.getActivePoints().size() == 1)
			spinner = new JSpinner(new SpinnerNumberModel(Integer.parseInt(Main.getCurrentPage().getDots().get(mdp.getActivePoints().get(0)).replaceAll("[A-Za-z]", "")), 1, Integer.MAX_VALUE, 1));
		else {
			spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
			spinner.setEnabled(false);
		}

		spinner.addChangeListener((ChangeEvent e) -> Main.getCurrentPage().getDots().put(mdp.getActivePoints().get(0), (String) instrument.getSelectedItem() + (int) spinner.getValue()));
		return spinner;
	}

	/**
	 * Initializes xPos to be the current dot's x position
	 *
	 * @return xPos
	 */
	private JSpinner getXPos() {
		Rectangle field = DS2DesktopPane.getField();
		JSpinner spinner;
		if (mdp.getActivePoints().get(0) != null && mdp.getActivePoints().size() == 1)
			spinner = new JSpinner(new SpinnerNumberModel(mdp.getActivePoints().get(0).x, field.x, field.width + field.x, 1));
		else {
			spinner = new JSpinner(new SpinnerNumberModel(field.x, field.x, field.width + field.x, 1));
			spinner.setEnabled(false);
		}

		spinner.addChangeListener((ChangeEvent e) -> {
			deleting = true;
			Point old = new Point(mdp.getActivePoints().get(0));
			String str = Main.getCurrentPage().getDots().get(mdp.getActivePoints().get(0));
			Main.getCurrentPage().getDots().remove(mdp.getActivePoints().get(0));
			Point newPoint = new Point((int) xPos.getValue(), old.y);
			Main.getCurrentPage().getDots().put(newPoint, str);
			mdp.addActivePoint(newPoint);
			updatePosition();
			deleting = false;
		});
		return spinner;
	}

	/**
	 * Initializes yPos to be the current dot's y position
	 *
	 * @return yPos
	 */
	private JSpinner getYPos() {
		Rectangle field = DS2DesktopPane.getField();
		JSpinner spinner;
		if (mdp.getActivePoints().get(0) != null && mdp.getActivePoints().size() == 1)
			spinner = new JSpinner(new SpinnerNumberModel(mdp.getActivePoints().get(0).y, field.y, field.height + field.y, 1));
		else {
			spinner = new JSpinner(new SpinnerNumberModel(field.y, field.y, field.height + field.y, 1));
			spinner.setEnabled(false);
		}
		spinner.addChangeListener((ChangeEvent e) -> {
			deleting = true;
			Point old = new Point(mdp.getActivePoints().get(0));
			String str = Main.getCurrentPage().getDots().get(mdp.getActivePoints().get(0));
			Main.getCurrentPage().getDots().remove(mdp.getActivePoints().get(0));
			Point newPoint = new Point(old.x, (int) yPos.getValue());
			Main.getCurrentPage().getDots().put(newPoint, str);
			mdp.addActivePoint(newPoint);
			updatePosition();
			deleting = false;
		});
		return spinner;
	}

	/**
	 * Initializes positionText to be the position text of the current dot
	 *
	 * @return positionText
	 */
	private JTextArea getPositionText() {
		JTextArea area;
		if (mdp.getActivePoints().get(0) != null && mdp.getActivePoints().size() == 1)
			area = new JTextArea(getPointText(mdp.getActivePoints().get(0)));
		else
			area = new JTextArea(2, 40);
		area.setEditable(false);
		area.setEnabled(false);
		return area;
	}

	/**
	 * Gets the position text of the given point. Example:
	 * Horizontal - Side 1: 3.75 inside of 25
	 * Vertical - On Front Hash
	 *
	 * @param p the point
	 * @return the position text
	 */
	public static String getPointText(Point p) {
		Rectangle field = DS2DesktopPane.getField();
		double width = field.getWidth();
		double height = field.getHeight();
		double fiveYards = width / 20.0;
		double third = height / 3.0;
		double x = p.getX() - field.getX();
		double y = p.getY() - field.getY();

		String str = "Horizontal - ";
		if (x < width / 2)
			str += "Side 1: ";
		else
			str += "Side 2: ";

		double distance = Integer.MAX_VALUE;
		int best = 0;
		for (int i = 0; i <= 20; i++) {
			if (Math.abs(fiveYards * i - x) < distance) {
				distance = Math.abs(fiveYards * i - x);
				best = i;
			}
		}
		int closestYardLine = 50 - Math.abs(5 * best - 50);
		String side = (best * fiveYards > x) != (x < width / 2) ? " inside of " : " outside of ";

		double steps = (distance / fiveYards) * 8.0;
		steps = Math.round(steps * 4.0) / 4.0;

		str += steps + side + closestYardLine;
		str = str.replace("0.0 outside of", "On").replace("0.0 inside of", "On");

		if (str.contains("On 50"))
			str = "Horizontal - On 50";

		str += "\nVertical - ";

		distance = Integer.MAX_VALUE;
		best = 0;
		for (int i = 0; i <= 3; i++) {
			if (Math.abs(third * i - y) < distance) {
				distance = Math.abs(third * i - y);
				best = i;
			}
		}
		String[] hashes = new String[] { "Back Sideline", "Back Hash", "Front Hash", "Front Sideline" };

		side = (best * third > y) ? " behind " : " in front of ";
		steps = (distance / fiveYards) * 8.0;
		steps = Math.round(steps * 4.0) / 4.0;

		str += steps + side + hashes[best];
		str = str.replace("Vertical - 0.0 behind", "Vertical - On").replace("Vertical - 0.0 in front of", "Vertical - On");
		return str;
	}

	/**
	 * Sets position's text to be the position text of the current point
	 */
	private void updatePosition() {
		position.setText(getPointText(mdp.getActivePoints().get(0)));
	}

	/**
	 * Updates all components to be accurate to the current dot
	 */
	private void updateAll() {
		instrument.setEnabled(true);
		instrument.removeItemListener(instrument.getItemListeners()[0]);
		number.removeChangeListener(number.getChangeListeners()[0]);
		instrument.setSelectedItem(String.valueOf(Main.getCurrentPage().getDots().get(mdp.getActivePoints().get(0)).replaceAll("[0-9]", "")));
		number.setEnabled(true);
		number.setValue(Integer.parseInt(Main.getCurrentPage().getDots().get(mdp.getActivePoints().get(0)).replaceAll("[A-Za-z]", "")));

		Rectangle field = DS2DesktopPane.getField();

		xPos.setEnabled(true);
		xPos.setModel(new SpinnerNumberModel(mdp.getActivePoints().get(0).x, field.x, field.width + field.x, 1));
		xPos.setValue(mdp.getActivePoints().get(0).x);
		yPos.setEnabled(true);
		yPos.setModel(new SpinnerNumberModel(mdp.getActivePoints().get(0).y, field.y, field.height + field.y, 1));
		yPos.setValue(mdp.getActivePoints().get(0).y);
		position.setEnabled(true);
		updatePosition();

		instrument.addItemListener((ItemEvent e) -> Main.getCurrentPage().getDots().put(mdp.getActivePoints().get(0), (String) instrument.getSelectedItem() + (int) number.getValue()));
		number.addChangeListener((ChangeEvent e) -> Main.getCurrentPage().getDots().put(mdp.getActivePoints().get(0), (String) instrument.getSelectedItem() + (int) number.getValue()));
	}

	/**
	 * Updates all components if the current dot is defined, disables all components if it is not
	 *
	 * @param activePoints the current dot
	 */
	public void updateAll(Vector<Point> activePoints) {
		if (activePoints.get(0) == null || activePoints.size() != 1) {
			instrument.setEnabled(false);
			number.setEnabled(false);
			xPos.setEnabled(false);
			yPos.setEnabled(false);
			position.setEnabled(false);
		} else {
			updateAll();
		}
	}

	/**
	 * Returns if a point is being deleted
	 */
	public static boolean isDeleting() {
		return deleting;
	}
}
