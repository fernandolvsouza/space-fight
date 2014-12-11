package com.haters.games.render.swing;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haters.games.input.UserInputStream;
import com.haters.games.input.swing.SwingUserInputStream;
import com.haters.games.render.RenderEngine;

@SuppressWarnings("serial")
public class PanelJ2D extends JPanel implements RenderEngine {
	private static final Logger log = LoggerFactory.getLogger(PanelJ2D.class);

	public static final int SCREEN_DRAG_BUTTON = 3;

	private Graphics2D dbg = null;
	private Image dbImage = null;
	private UserInputStream  inputstream;
	

	public PanelJ2D(final SwingUserInputStream inputstream) {
		setBackground(Color.gray);
		setPreferredSize(new Dimension(getInitialWidth(), getInitialHeight()));
		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				//System.out.println(event);
				inputstream.addEventFromKeyEvent(event,true);
			}
			public void keyReleased(KeyEvent event) {
				//System.out.println(event);
				inputstream.addEventFromKeyEvent(event,false);				
			}
			
			
		});
		
	    this.inputstream = inputstream;
	}
	

	public Graphics2D getDBGraphics() {
		return dbg;
	}

	public boolean render() {
		if (dbImage == null) {
			log.debug("dbImage is null, creating a new one");
			if (getInitialWidth() <= 0 || getInitialHeight() <= 0) {
				return false;
			}
			dbImage = createImage(getInitialWidth() , getInitialHeight());
			if (dbImage == null) {
				log.error("dbImage is still null, ignoring render call");
				return false;
			}
			dbg = (Graphics2D) dbImage.getGraphics();
			dbg.setFont(new Font("Courier New", Font.PLAIN, 12));
			System.out.println("dbg:" + dbg);
		}
		dbg.setColor(Color.black);
		dbg.fillRect(0, 0, getInitialWidth(), getInitialHeight());
		return true;
	}

	public void paintScreen() {
		try {
			Graphics g = this.getGraphics();
			if ((g != null) && dbImage != null) {
				g.drawImage(dbImage, 0, 0, null);
				Toolkit.getDefaultToolkit().sync();
				g.dispose();
			}
		} catch (AWTError e) {
			log.error("Graphics context error", e);
		}
	}

	public void drawString(String string, int i, int j) {
		dbg.drawString(string, i, j);
	}

	@Override
	public int getInitialHeight() {
		return 600;
	}

	@Override
	public int getInitialWidth() {
		return 600;
	}


	@Override
	public UserInputStream getUserInputStream() {
		return inputstream;
	}
}
