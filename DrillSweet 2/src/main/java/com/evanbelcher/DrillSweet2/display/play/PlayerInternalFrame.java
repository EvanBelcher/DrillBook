package com.evanbelcher.DrillSweet2.display.play;

import com.evanbelcher.DrillSweet2.Main;
import com.evanbelcher.DrillSweet2.display.GraphicsRunner;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class PlayerInternalFrame extends JInternalFrame {

	private String state;

	public PlayerInternalFrame(PagePlayer pagePlayer, GraphicsRunner graphicsRunner) {
		super("Player", false, false, false, true);

		state = "pause";

		JButton start = new JButton(getIcon("start"));
		start.addActionListener((ActionEvent e) -> {
			state = "pause";
			for (MovingPoint p : pagePlayer.getPoints().keySet())
				p.reset();
		});
		JButton rewind = new JButton(getIcon("rewind"));
		rewind.addActionListener((ActionEvent e) -> state = "rewind");
		JButton play = new JButton(getIcon("play"));
		play.addActionListener((ActionEvent e) -> state = "play");
		JButton pause = new JButton(getIcon("pause"));
		pause.addActionListener((ActionEvent e) -> state = "pause");
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
		add(play);
		add(pause);
		add(end);
		add(exit);

		pack();
	}

	public String getState() {
		return state;
	}

	public ImageIcon getIcon(String s) {
		try {
			return new ImageIcon(ImageIO.read(Main.getFile(s + ".png", this)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
