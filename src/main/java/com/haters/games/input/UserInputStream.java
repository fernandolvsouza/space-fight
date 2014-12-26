package com.haters.games.input;


public interface UserInputStream {

	boolean hasTurnLeftEvent();
	boolean hasTurnRightEvent();
	boolean hasAccelerationEvent();
	boolean hasBreakEvent();
	boolean hasFireEvent();
	void processEvents();
}
