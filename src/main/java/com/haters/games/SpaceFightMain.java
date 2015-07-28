package com.haters.games;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.haters.games.input.swing.SwingUserInputStream;
import com.haters.games.render.swing.DebugDrawJ2D;
import com.haters.games.render.swing.PanelJ2D;


public class SpaceFightMain {

	public static void main(String[] args) {

		/*final SwingUserInputStream stream = new SwingUserInputStream();
		PanelJ2D panel = new PanelJ2D(stream);
		
	    JFrame jframe = new JFrame();
	    jframe.setTitle("SpaceFight");
	    jframe.setLayout(new BorderLayout());
	    jframe.add((Component) panel, "Center");
	    jframe.pack();
	    jframe.setVisible(true);
	    jframe.setFocusable(true);
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    	    
	    DebugDrawJ2D debugDraw = new DebugDrawJ2D(panel, true);
	    */
	    final GameController controller = new GameController();
	    
	    //System.out.println(System.getProperty("java.home"));

	    SwingUtilities.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	    	  controller.start();
	      }
	    });

	}

}
