package com.haters.games.render;

import com.haters.games.input.GameInputStream;


public interface RenderEngine {
	public int getInitialHeight();
	public int getInitialWidth();	
	public boolean render();
	public void paintScreen();
	public GameInputStream getUserInputStream();
	public void grabFocus();
	
}
