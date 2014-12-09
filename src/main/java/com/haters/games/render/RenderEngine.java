package com.haters.games.render;


public interface RenderEngine {
	public int getInitialHeight();
	public int getInitialWidth();	
	public void render(Runnable block);
	public boolean render();
	public void paintScreen();
}
