package com.haters.games.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;

import com.haters.games.input.GameInputStream;
import com.haters.games.output.NetworkOutputStream;

/**
 * @author Fernando Valente
 */

public class GameLogic {
	
	private static final int numberOfBots = 100;
	
	private final List<SpaceShip> bots = new ArrayList<SpaceShip>();
	private final List<Destroyable> killthen = new ArrayList<Destroyable>();
	
	private SpaceWorld spaceWorld;
	private List<SpaceShip> players = new ArrayList<SpaceShip>(100);
	private GameInputStream istream;
	private NetworkOutputStream ostream;
	
	private int planeSequence = 0;
	
	public GameLogic(SpaceWorld spaceWorld, GameInputStream stream, NetworkOutputStream outputStream) {
		this.spaceWorld = spaceWorld;
		this.istream = stream;
		this.ostream = outputStream;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(killthen));
		
		for(int i=0;i<numberOfBots;i++){
			SpaceShip bot = PolygonSpaceShip.create(spaceWorld.getWorld(), spaceWorld.getRandomPosition(), planeSequence++, killthen);
			bots.add(bot);			
		}

	}

	public void step(float f, int velocityIterations, int positionIterations) {
		spaceWorld.step(f, velocityIterations, positionIterations);

		for(SpaceShip bot : bots){
			if(bot.getCurrentEnergy() <= 0){
				killthen.add((Destroyable)bot);
				bots.remove(bot);
				break;
			}
			bot.detectGameEntities();
			//bot.setAttackMode();
			
			Set<SpaceShip> enemies = bot.getShipsInRange();
			//Boundaries bounds =  bot.getBoundsInRange();
			if(bot.avoidColision()){
				if(enemies.size() !=0){
					bot.rotateTo(enemies.iterator().next().getBody().getPosition());
				}
			}
			
			if(bot.shouldFire(enemies)){	
				bot.fireToPosition(null);
			}

			bot.accelerate(AccelerationState.UP);
			
		}



		if (istream.hasNewPlayerEvent()) {
			for(Integer id : istream.getNewPlayers()) {
				players.add((SpaceShip)CircleSpaceShip.create(spaceWorld.getWorld(), id, killthen, false));
			}
			istream.eraseNewPlayersEvents();
		}

		if (istream.hasRemovePlayerEvent()) {
			for(Integer id : istream.getRemovePlayers()) {
				for (int i = 0; i < players.size(); i++) {
					if (players.get(i).getId() == id) {
						killthen.add((Destroyable)players.get(i));
						players.remove(i);
						i--;
					}
				}
			}
			System.out.println("players size : " +  players.size());
			istream.eraseRemovePlayersEvents();
		}

		for (int i = 0; i < players.size(); i++) {
			SpaceShip player = this.players.get(i);
			if (player.getCurrentEnergy() <= 0) {
				killthen.add((Destroyable)player);
				players.remove(player);
				i--;
				continue;
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
				float x = istream.getMouseMoveEvent(player)[0];
				float y = istream.getMouseMoveEvent(player)[1];

				Vec2 to = new Vec2(x,y);
				player.fireToPosition(to);
			}

			player.detectGameEntities();
		}

	}
	
	public void afterStep(){
		for (Destroyable object : killthen) {
			object.destroy();
			object = null;
		}
		
		killthen.clear();
		ostream.streamGame(spaceWorld,bots,players);
	
	}
	 
}
