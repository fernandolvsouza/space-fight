package com.haters.games.input;


public interface GameInputStream {

	boolean hasTurnLeftEvent();
	boolean hasTurnRightEvent();
	boolean hasAccelerationEvent();
	boolean hasBreakEvent();
	boolean hasFireEvent();
	boolean hasNewPlayerEvent();
	void processEvents();
}
