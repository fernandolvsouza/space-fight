package com.haters.games.input.swing;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

import com.haters.games.input.GameInputStream;

public class SwingUserInputStream implements GameInputStream {
	
	private LinkedList<InputEventType> events =	new LinkedList<InputEventType>();
	private boolean[] eventsBitMap = new boolean[84];

	
	public SwingUserInputStream() {
		for (int i = 0; i < eventsBitMap.length; i++) {
			eventsBitMap[i] = false;			
		}
	}
	

	@Override
	public boolean hasTurnLeftEvent() {
		return eventsBitMap[InputEventType.TURNLEFT_ON.keycode] ;
	}

	@Override
	public boolean hasTurnRightEvent() {
		return eventsBitMap[InputEventType.TURNRIGHT_ON.keycode];
	}

	@Override
	public boolean hasAccelerationEvent() {
		return eventsBitMap[InputEventType.ACCELERATE_ON.keycode];
	}

	@Override
	public boolean hasBreakEvent() {
		return eventsBitMap[InputEventType.BREAK_ON.keycode];
	}

	@Override
	public boolean hasFireEvent() {
		return eventsBitMap[InputEventType.FIRE_ON.keycode];
	}
	
	public void pushEvent(InputEventType e){
		synchronized (events) {
			events.add(e);	
		}
	}
		
	@Override
	public void processEvents(){

		synchronized (events) {
			while(!events.isEmpty()){
				InputEventType event = events.pop();
				eventsBitMap[event.keycode] = event.on;
			}
			events.clear();
		}		
	}
	
	public void addEventFromKeyEvent(KeyEvent event,boolean enable) {
		InputEventType e = InputEventType.getByKeyCode(event.getKeyCode(),enable);
		if(e != null){	
			pushEvent(e);
		}

	}
	
	public enum InputEventType {
		TURNRIGHT_ON(39,true),TURNLEFT_ON(37,true),ACCELERATE_ON(38,true),BREAK_ON(40,true),FIRE_ON(83,true),
		TURNRIGHT_OFF(39,false),TURNLEFT_OFF(37,false), ACCELERATE_OFF(38,false), BREAK_OFF(40,false), FIRE_OFF(83,false);
		
		public int keycode;
		public boolean on;
			
		
		private InputEventType(int keycode,boolean enable) {
			this.keycode = keycode;
			this.on = enable;
		}
		
		public static InputEventType getByKeyCode(int keycode, boolean on) {
			for (InputEventType e : values()) {
				if (e.keycode ==keycode && e.on == on)
					return e;
			}
			return null;
		}
	}
}
