package com.haters.games.physics;

import java.util.ArrayList;
import java.util.List;

public class DestroyPool {
	
	private final List<Destroyable> killthen = new ArrayList<Destroyable>();

	public  void add(Destroyable e){
		killthen.add(e);
		e.setReadyToDestroy(true);
	}	
	
	public void destroyAll(){
		for (Destroyable object : killthen) {
			object.destroy();
			object = null;
		}
		
		killthen.clear();
	}
}
