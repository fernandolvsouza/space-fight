package com.haters.games.input;


import com.haters.games.physics.PolygonSpaceShip;
import com.haters.games.physics.SpaceShip;

import java.util.List;

public interface GameInputStream {

	float[] getMouseMoveEvent(SpaceShip player);

	boolean hasTurnLeftEvent(SpaceShip player);
	boolean hasTurnRightEvent(SpaceShip player);
	boolean hasAccelerationEvent(SpaceShip player);
	boolean hasBreakEvent(SpaceShip player);
	boolean hasFireEvent(SpaceShip player);
	boolean hasNewPlayerEvent();
	boolean hasRemovePlayerEvent();

	public List<Integer> getNewPlayers();
	public List<Integer> getRemovePlayers();
	public void eraseNewPlayersEvents();
	public void eraseRemovePlayersEvents();

	void processEvents();
}
