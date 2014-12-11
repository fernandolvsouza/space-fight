/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright notice,
 * 	  this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright notice,
 * 	  this list of conditions and the following disclaimer in the documentation
 * 	  and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/**
 * Created at 7:50:04 AM Jan 20, 2011
 */
package com.haters.games.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.haters.games.GameController;
import com.haters.games.input.UserInputStream;

/**
 * @author Fernando Valente
 */

public class GameLogic {
	private Plane plane;
	private List<Plane> bots = new ArrayList<Plane>();
	private List<Destroyable> killthen = new ArrayList<Destroyable>();
	private SpaceWorld spaceWorld;
	private int numberOfBots = 10;
	private  GameController controller;
	private UserInputStream stream;
	
	public GameLogic(SpaceWorld spaceWorld, GameController controller, UserInputStream stream) {
		this.spaceWorld = spaceWorld;
		this.controller = controller;
		this.stream = stream;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(killthen));
		
		for(int i=0;i<numberOfBots;i++){
			bots.add(Plane.create(spaceWorld.getWorld(), spaceWorld.getRandomPosition(),i,killthen));
		}
		
		plane = Plane.create(spaceWorld.getWorld(),numberOfBots,killthen);
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		spaceWorld.step(f, velocityIterations, positionIterations);	

		for(Plane bot : bots){
			if(bot.getCurrentEnergy() <= 0){
				//bot.destroy(); //TODO o q fazer bullets restantes? destruir? destrui depois de um tempo? ou deixar para sempre?
				killthen.add(bot);
				bots.remove(bot);
				break;
			}
			DetectEnemiesCallback callback = (DetectEnemiesCallback)bot.detectEnemy();
			//callback.debug(this.debugDraw);
			for(Plane enemy :  callback.enemies){
				bot.rotateToEnemy(enemy,null);

				if(bot.shouldFire(enemy)){	
					bot.fire();
				}
				break;
			}
			bot.accelerate(AccelerationState.UP);	
		}
		
		if(plane.getCurrentEnergy() <= 0){
			//addTextLine("FIM FIM FIM FIM FIM");
		}
		
		if (stream.hasTurnLeftEvent()) { //37
			plane.turn(TurnState.LEFT);	
		}
		
		if (stream.hasTurnRightEvent()) { //39
			plane.turn(TurnState.RIGHT);	
		}
		
		if (stream.hasAccelerationEvent()) { //38
			plane.accelerate(AccelerationState.UP);	
		}
		
		if (stream.hasBreakEvent()) { //s/40
			plane.accelerate(AccelerationState.DOWN);		
		}
		
		if (stream.hasFireEvent()){ //'s'
			plane.fire();
		}
		controller.setCamera(plane.getBody().getPosition());
		spaceWorld.drawDebugData();
	}
	
	public void afterStep(){
		//System.out.println("killthen" + killthen.size());
		for (Destroyable object : killthen) {
			object.destroy();
			object = null;
		}
		
		killthen.clear();
	
	}
}
