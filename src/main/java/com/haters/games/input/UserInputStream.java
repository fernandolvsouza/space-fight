package com.haters.games.input;

import com.haters.games.input.swing.SwingUserInputStream.InputEventType;

public interface UserInputStream {

	boolean hasTurnLeftEvent();
	boolean hasTurnRightEvent();
	boolean hasAccelerationEvent();
	boolean hasBreakEvent();
	boolean hasFireEvent();
	void pushEvent(InputEventType type);
	void processEvents();
}
