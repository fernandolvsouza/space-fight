package com.haters.games.render;

import com.haters.games.input.UserInputStream;


public interface RenderEngine {
	public int getInitialHeight();
	public int getInitialWidth();	
	public boolean render();
	public void paintScreen();
	public UserInputStream getUserInputStream();
	public void grabFocus();
	
}
