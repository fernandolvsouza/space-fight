package com.haters.games.input;


import com.haters.games.physics.SpaceShip;

import java.util.List;

public interface GameInputStream {

	boolean hasTurnLeftEvent(SpaceShip player);
	boolean hasTurnRightEvent(SpaceShip player);
	boolean hasAccelerationEvent(SpaceShip player);
	boolean hasBreakEvent(SpaceShip player);
	boolean hasFireEvent(SpaceShip player);
	boolean hasNewPlayerEvent();

	public List<Integer> getNewPlayers();
	public void eraseNewPlayersEvents();

	void processEvents();
}
