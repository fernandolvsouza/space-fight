package com.haters.games.physics;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;

import com.haters.games.input.GameInputStream;
import com.haters.games.output.NetworkOutputStream;

/**
 * @author Fernando Valente
 */

public class GameLogic {

	private static final int numberOfBots = 10;
	
	private final List<SpaceShip> bots = new ArrayList<SpaceShip>();
	private final List<Destroyable> killthen = new ArrayList<Destroyable>();
	
	private SpaceWorld spaceWorld;
	private List<SpaceShip> players = new ArrayList<SpaceShip>(100);
	private GameInputStream istream;
	private NetworkOutputStream ostream;
	private long spawnbotsfrequency = 60000;
	private long lastspawntime = 0;
	

	
	public GameLogic(SpaceWorld spaceWorld, GameInputStream stream, NetworkOutputStream outputStream) {
		this.spaceWorld = spaceWorld;
		this.istream = stream;
		this.ostream = outputStream;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(killthen));
		
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		spaceWorld.step(f, velocityIterations, positionIterations);
		
		
		long now = new Date().getTime();
		if(now - lastspawntime > spawnbotsfrequency){
			int createbots = numberOfBots-bots.size();
			for(int i=0;i<createbots;i++){
				SpaceShip bot = CircleSpaceShip.create(spaceWorld.getWorld(), spaceWorld.getRandomPosition(), Sequence.getSequence(), killthen).setCruiseMode();
				bots.add(bot);			
			}
			lastspawntime = now;
		}
		
		
		for(SpaceShip bot : bots){
			if(bot.getCurrentEnergy() <= 0){
				killthen.add((Destroyable)bot);
				bots.remove(bot);
				break;
			}
			bot.detectGameEntities();
			//bot.setAttackMode();
			
			Set<SpaceShip> ships = bot.getShipsInRange();
			Set<SpaceShip> players = new LinkedHashSet<SpaceShip>(); 
			
			for (SpaceShip ship : ships) {
				if(!ship.isbot()){
					players.add(ship);
				}
			}
			//Boundaries bounds =  bot.getBoundsInRange();
			if(bot.avoidColision()){
				if(players.size() !=0){
					bot.rotateTo(players.iterator().next().getBody().getPosition());
				}
			}
			
			if(bot.shouldFire(players)){	
				bot.fire();
			}

			bot.up();
			
		}



		if (istream.hasNewPlayerEvent()) {
			for(Integer id : istream.getNewPlayers()) {
				players.add((SpaceShip)CircleSpaceShip.create(spaceWorld.getWorld(), id, killthen, false).setAttackMode());
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
			player.heal();
		
			if (istream.getMouseMoveEvent(player) != null) {
				float x = istream.getMouseMoveEvent(player)[0];
				float y = istream.getMouseMoveEvent(player)[1];
				player.setMousePosition(new Vec2(x,y));
			}
			
			if (player.getCurrentEnergy() <= 0) {
				killthen.add((Destroyable)player);
				players.remove(player);
				i--;
				continue;
			}
			if (istream.hasLeftEvent(player)) { //37
				player.left();
			}

			if (istream.hasRightEvent(player)) { //39
				player.right();
			}

			if (istream.hasUpEvent(player)) { //38
				player.up();
			}

			if (istream.hasDownEvent(player)) { //40
				player.down();
			}

			if (istream.hasFireEvent(player)) { //'s'
				player.fire();
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
