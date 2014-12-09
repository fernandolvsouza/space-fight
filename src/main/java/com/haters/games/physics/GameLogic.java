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
import java.util.List;

import com.haters.games.GameController;

/**
 * @author Fernando Valente
 */



public class GameLogic {
	private Plane plane;
	private List<Plane> bots = new ArrayList<Plane>();
	private List<Bullet> bulletsToDie = new ArrayList<Bullet>();
	private SpaceWorld spaceWorld;
	private int numberOfBots = 10;
	private  GameController controller;
	
	public GameLogic(SpaceWorld spaceWorld, GameController controller) {
		this.spaceWorld = spaceWorld;
		this.controller = controller;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(bulletsToDie));
		
		for(int i=0;i<numberOfBots;i++){
			bots.add(Plane.create(spaceWorld.getWorld(), spaceWorld.getRandomPosition(),i));
		}
		
		plane = Plane.create(spaceWorld.getWorld(),numberOfBots);
	}

	public void step() {

		for(Plane bot : bots){
			if(bot.getCurrentEnergy() <= 0){
				bot.destroy(); //TODO o q fazer bullets restantes? destruir? destrui depois de um tempo? ou deixar para sempre?
				bots.remove(bot);
				bot = null;
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
		
		/*if(plane.getCurrentEnergy() <= 0){
			addTextLine("FIM FIM FIM FIM FIM");
		}
		
		if (getModel().getCodedKeys()[37]) {
			plane.turn(TurnState.LEFT);	
		}
		
		if (getModel().getCodedKeys()[39]) {
			plane.turn(TurnState.RIGHT);	
		}
		
		if (getModel().getCodedKeys()[38]) {
			plane.accelerate(AccelerationState.UP);	
		}
		
		if (getModel().getCodedKeys()[40]) {
			plane.accelerate(AccelerationState.DOWN);		
		}
		
		if (getModel().getKeys()['s']){
			plane.fire();
		}*/
		controller.setCamera(plane.getBody().getPosition());
	}
}
