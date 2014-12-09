package com.haters.games;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.haters.games.render.swing.DebugDrawJ2D;
import com.haters.games.render.swing.PanelJ2D;


public class SpaceFightMain {

	public static void main(String[] args) {

		PanelJ2D panel = new PanelJ2D();
	    JFrame jframe = new JFrame();
	    jframe.setTitle("SpaceFight Sandbox");
	    jframe.setLayout(new BorderLayout());
	    jframe.add((Component) panel, "Center");
	    jframe.pack();
	    jframe.setVisible(true);
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	    
	    DebugDrawJ2D debugDraw = new DebugDrawJ2D(panel, true);
	    
	    final GameController loop = new GameController(panel,debugDraw);
	    
	    //System.out.println(System.getProperty("java.home"));

	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	    	  loop.run();
	      }
	    });

	}

}
