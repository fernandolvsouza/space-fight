package com.haters.games.physics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.haters.games.GameController;
import com.haters.games.input.GameInputStream;
import com.haters.games.output.NetworkOutputStream;
import com.haters.games.output.GameSerializer;

/**
 * @author Fernando Valente
 */

public class GameLogic {
	
	private static final int numberOfBots = 0;
	
	private final List<SpaceShip> bots = new ArrayList<SpaceShip>();
	private final List<Destroyable> killthen = new ArrayList<Destroyable>();
	
	private SpaceWorld spaceWorld;
	private List<SpaceShip> players = new ArrayList<SpaceShip>(100);
	private GameController controller;
	private GameInputStream istream;
	private NetworkOutputStream ostream;
	private GameSerializer  serializer;
	//private NetworkInputStream  istream;
	
	private int planeSequence = 0;
	
	public GameLogic(SpaceWorld spaceWorld, GameController controller, GameInputStream stream, NetworkOutputStream outputStream, GameSerializer serializer) {
		this.spaceWorld = spaceWorld;
		this.controller = controller;
		this.istream = stream;
		this.ostream = outputStream;
		this.serializer = serializer;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(killthen));
		
		for(int i=0;i<numberOfBots;i++){
			SpaceShip bot = SpaceShip.create(spaceWorld.getWorld(), spaceWorld.getRandomPosition(),planeSequence++,killthen);
			bots.add(bot);			
		}

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


		if (istream.hasNewPlayerEvent()) {
			for(Integer id : istream.getNewPlayers()) {
				players.add(SpaceShip.create(spaceWorld.getWorld(), id, killthen, false).setAttackMode());
			}
			istream.eraseNewPlayersEvents();
		}

		for(SpaceShip player : players) {
			if (player.getCurrentEnergy() <= 0) {
				killthen.add(player);
			}
			if (istream.hasTurnLeftEvent(player)) { //37
				player.turn(TurnState.LEFT);
			}

			if (istream.hasTurnRightEvent(player)) { //39
				player.turn(TurnState.RIGHT);
			}

			if (istream.hasAccelerationEvent(player)) { //38
				player.accelerate(AccelerationState.UP);
			}

			if (istream.hasBreakEvent(player)) { //40
				player.accelerate(AccelerationState.DOWN);
			}

			if (istream.hasFireEvent(player)) { //'s'
				player.fire();
			}
		}
		if(!players.isEmpty()) {
			controller.setCamera(players.get(0).getBody().getPosition());
		}
		spaceWorld.drawDebugData();		
	}
	
	public void afterStep(){
		for (Destroyable object : killthen) {
			object.destroy();
			object = null;
		}
		
		killthen.clear();
		
		try {
			
			serializer.serialize(spaceWorld,bots,players,ostream.getWriter());
			//istream.checkInput();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	 
}
