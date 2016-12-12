package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.display.GraphicsRunner;
import com.evanbelcher.DrillSweet2.display.data.play.MovingPoint;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * The JInternalFrame to hold the Player controls
 *
 * @author Evan Belcher
 */
public class PlayerInternalFrame extends JInternalFrame {

	private String state;

	/**
	 * Creates the object. Creates and adds all of the buttons.
	 *
	 * @param pagePlayer     the PagePlayer that has the points
	 * @param graphicsRunner the GraphicsRunner that put the application into Play mode
	 */
	public PlayerInternalFrame(PagePlayer pagePlayer, GraphicsRunner graphicsRunner) {
		super("Player", false, false, false, true);
		setFrameIcon(null);

		state = "pause";

		JButton start = new JButton(getIcon("start"));
		start.addActionListener((ActionEvent e) -> {
			state = "pause";
			for (MovingPoint p : pagePlayer.getPoints().keySet())
				p.start();
		});
		JButton rewind = new JButton(getIcon("rewind"));
		rewind.addActionListener((ActionEvent e) -> state = "rewind");
		JButton play = new JButton(getIcon("play"));
		play.addActionListener((ActionEvent e) -> state = "play");
		JButton pause = new JButton(getIcon("pause"));
		pause.addActionListener((ActionEvent e) -> state = "pause");
		JButton help = new JButton(getIcon("help"));
		help.addActionListener((ActionEvent e) -> JOptionPane.showMessageDialog(this, "Black: Normal dot\nRed: Long distance move\nBlue: Colliding dot", "Help", JOptionPane.INFORMATION_MESSAGE));
		JButton end = new JButton(getIcon("end"));
		end.addActionListener((ActionEvent e) -> {
			state = "pause";
			for (MovingPoint p : pagePlayer.getPoints().keySet())
				p.end();
		});

		JButton exit = new JButton(getIcon("exit"));
		exit.addActionListener((ActionEvent e) -> graphicsRunner.toNormalMode());

		setLayout(new MigLayout());
		add(start);
		add(rewind);
		add(pause);
		add(play);
		add(end);
		add(help);
		add(exit);

		pack();

		//Set the window's location.
		setLocation(GraphicsRunner.SCREEN_SIZE.width - getSize().width, 0);
	}

	/**
	 * Returns the play state
	 * "play" -> plays forward
	 * "rewind" -> plays backward
	 * "pause" -> stops playing in place
	 */
	public String getState() {
		return state;
	}

	/**
	 * Gets the icon from resources
	 *
	 * @param s the name of the file
	 * @return the icon image
	 */
	public ImageIcon getIcon(String s) {
		try {
			return new ImageIcon(ImageIO.read(Main.getFile(s + ".png", this)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
