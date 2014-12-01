package com.haters.games.render.swing;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haters.games.render.RenderEngine;

@SuppressWarnings("serial")
public class PanelJ2D extends JPanel implements RenderEngine {
	private static final Logger log = LoggerFactory.getLogger(PanelJ2D.class);

	public static final int SCREEN_DRAG_BUTTON = 3;

	public static final int INIT_WIDTH = 1200;
	public static final int INIT_HEIGHT = 1200;

	private Graphics2D dbg = null;
	private Image dbImage = null;

	private int panelWidth;
	private int panelHeight;

	public PanelJ2D() {
		setBackground(Color.black);
		setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
		updateSize(INIT_WIDTH, INIT_HEIGHT);

	}

	public Graphics2D getDBGraphics() {
		return dbg;
	}

	private void updateSize(int width, int height) {
		panelWidth = width;
		panelHeight = height;
	}

	public boolean render() {
		if (dbImage == null) {
			log.debug("dbImage is null, creating a new one");
			if (panelWidth <= 0 || panelHeight <= 0) {
				return false;
			}
			dbImage = createImage(panelWidth, panelHeight);
			if (dbImage == null) {
				log.error("dbImage is still null, ignoring render call");
				return false;
			}
			dbg = (Graphics2D) dbImage.getGraphics();
			dbg.setFont(new Font("Courier New", Font.PLAIN, 12));
			System.out.println("dbg:" + dbg);
		}
		dbg.setColor(Color.black);
		dbg.fillRect(0, 0, panelWidth, panelHeight);

		dbg.setColor(Color.white);
		dbg.drawString("teste", 0, 10);
		dbg.drawString("teste1", 0, 20);
		dbg.drawString("teste2", 0, 30);
		dbg.drawString("teste3", 0, 40);
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

	@Override
	public void render(Runnable block) {
		System.out.println("Render begin");
		if (render()) {
			block.run();
			paintScreen();
			System.out.println("Paint");
		}
		System.out.println("Render end");
	}

	public void drawString(String string, int i, int j) {
		dbg.drawString(string, i, j);
	}
}
