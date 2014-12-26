package com.haters.games.input;

import java.util.LinkedList;

import com.haters.games.input.swing.SwingUserInputStream.InputEventType;

public class NetworkInputStream implements UserInputStream {

	private LinkedList<InputEventType> events =	new LinkedList<InputEventType>();
	private boolean[] eventsBitMap = new boolean[4];

	
	@Override
	public boolean hasTurnLeftEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasTurnRightEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAccelerationEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasBreakEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasFireEvent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processEvents() {
		// TODO Auto-generated method stub

	}

}
