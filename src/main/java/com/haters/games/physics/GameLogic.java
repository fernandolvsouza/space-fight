package com.haters.games.physics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.haters.games.GameController;
import com.haters.games.input.UserInputStream;
import com.haters.games.output.GameSerializer;
import com.haters.games.output.OutputStream;

/**
 * @author Fernando Valente
 */

public class GameLogic {
	
	private static final int numberOfBots = 10;
	
	private final List<SpaceShip> bots = new ArrayList<SpaceShip>();
	private final List<Destroyable> killthen = new ArrayList<Destroyable>();
	
	private SpaceWorld spaceWorld;
	private SpaceShip player;
	private  GameController controller;
	private UserInputStream stream;
	private OutputStream ostream;
	
	private int planeSequence = 0;
	
	public GameLogic(SpaceWorld spaceWorld, GameController controller, UserInputStream stream, OutputStream outputStream) {
		this.spaceWorld = spaceWorld;
		this.controller = controller;
		this.stream = stream;
		this.ostream = outputStream;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(killthen));
		
		for(int i=0;i<numberOfBots;i++){
			SpaceShip bot = SpaceShip.create(spaceWorld.getWorld(), spaceWorld.getRandomPosition(),planeSequence++,killthen);
			bots.add(bot);			
		}
		
		player = SpaceShip.create(spaceWorld.getWorld(),planeSequence++,killthen,false);
		player.setAttackMode();
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		spaceWorld.step(f, velocityIterations, positionIterations);	

		for(SpaceShip bot : bots){
			if(bot.getCurrentEnergy() <= 0){
				killthen.add(bot);
				bots.remove(bot);
				break;
			}
			bot.detectGameEntities();
			//bot.setAttackMode();
			
			Set<SpaceShip> enemies = bot.getEnemiesInRange();
			//Boundaries bounds =  bot.getBoundsInRange();
			if(bot.avoidColision(spaceWorld.getDebugDraw())){
				if(enemies.size() !=0){
					bot.rotateTo(enemies.iterator().next().getBody().getPosition());
				}
			}
			
			if(bot.shouldFire(enemies)){	
				bot.fire();
			}

			bot.accelerate(AccelerationState.UP);
			
		}
		//plane.detectGameEntities();
		//plane.avoidBoundaries(spaceWorld.getDebugDraw());
		if(player.getCurrentEnergy() <= 0){
			killthen.add(player);
		}
		
		if (stream.hasTurnLeftEvent()) { //37
			player.turn(TurnState.LEFT);	
		}
		
		if (stream.hasTurnRightEvent()) { //39
			player.turn(TurnState.RIGHT);	
		}
		
		if (stream.hasAccelerationEvent()) { //38
			player.accelerate(AccelerationState.UP);	
		}
		
		if (stream.hasBreakEvent()) { //40
			player.accelerate(AccelerationState.DOWN);		
		}
		
		if (stream.hasFireEvent()){ //'s'
			player.fire();
		}
		controller.setCamera(player.getBody().getPosition());
		spaceWorld.drawDebugData();		
	}
	
	public void afterStep(){
		for (Destroyable object : killthen) {
			object.destroy();
			object = null;
		}
		
		killthen.clear();
		
		try {
			new GameSerializer().serialize(spaceWorld,bots,player,ostream.getWriter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	 
}
