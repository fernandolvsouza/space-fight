package com.haters.games.render;


public interface RenderEngine {
	public void render(Runnable block);
	public boolean render();
	public void paintScreen();
}
