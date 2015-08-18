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
	boolean hasNewPlayerEvent();
	boolean hasRemovePlayerEvent();

	public List<Integer> getNewPlayers();
	public List<Integer> getRemovePlayers();
	public void eraseNewPlayersEvents();
	public void eraseRemovePlayersEvents();

	void processEvents();
}
