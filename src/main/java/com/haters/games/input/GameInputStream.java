package com.haters.games.input;


import java.util.List;

import com.haters.games.physics.SpaceShip;

public interface GameInputStream {

	float[] getMouseMoveEvent(SpaceShip player);

	boolean hasLeftEvent(SpaceShip player);
	boolean hasRightEvent(SpaceShip player);
	boolean hasUpEvent(SpaceShip player);
	boolean hasDownEvent(SpaceShip player);
	boolean hasFireEvent(SpaceShip player);
	boolean hasBeBornEvent(SpaceShip player);
	boolean hasRemovePlayerEvent(SpaceShip player);
	
	boolean hasNewPlayerEvent();

	public List<Integer> getNewPlayers();
	public void eraseNewPlayersEvents();
	public void eraseRemovePlayersEvents();
	void eraseBeBornPlayersEvents();
	
	void processEvents();



	
}
