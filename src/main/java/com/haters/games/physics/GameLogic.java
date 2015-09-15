package com.haters.games.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;

import com.haters.games.input.GameInputStream;
import com.haters.games.output.NetworkOutputStream;

/**
 * @author Fernando Valente
 */

public class GameLogic {

	private static final int numberOfBots = 30;
	private static final int numberOfGarbage = 20;
	
	private final List<SpaceShip> bots = new ArrayList<SpaceShip>();
	private final DestroyPool destroypool = new DestroyPool();
	
	private SpaceWorld spaceWorld;
	private List<SpaceShip> players = new ArrayList<SpaceShip>(1000);
	private List<Energy> energies = new ArrayList<Energy>(numberOfGarbage);
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
		spaceWorld.getWorld().setContactListener(new CollisionCallback(destroypool));
		for(int i=0;i<numberOfGarbage;i++){
			energies.add(new Energy(spaceWorld));
		}
		
	}
	
	public void step(float f, int velocityIterations, int positionIterations) {
		spaceWorld.step(f, velocityIterations, positionIterations);
		
		
		long now = new Date().getTime();
		if(now - lastspawntime > spawnbotsfrequency){
			int createbots = numberOfBots-bots.size();
			for(int i=0;i<createbots;i++){
				SpaceShip bot = CircleSpaceShip.create(spaceWorld, spaceWorld.getRandomPosition(), Sequence.getSequence(), destroypool).setAttackMode();
				bots.add(bot);			
			}
			lastspawntime = now;
		}
		
		for (int i = 0; i < bots.size(); i++) {
			SpaceShip bot = bots.get(i);			
			
			if(bot.getCurrentEnergy() <= 0){
				destroypool.add((Destroyable)bot);
				bots.remove(bot);
				i--;
				continue;
			}
			bot.detectGameEntities();
			//bot.setAttackMode();
			
			Set<SpaceShip> alivePlayers = bot.getAlivePlayersInRange();


			//Boundaries bounds =  bot.getBoundsInRange();
			if(bot.avoidColision()){
				if(alivePlayers.size() !=0){
					bot.rotateTo(alivePlayers.iterator().next().getBody().getPosition());
				}
			}
			
			if(bot.shouldFire(alivePlayers)){	
				bot.fire();
			}

			bot.up();
		}

		if (istream.hasNewPlayerEvent()) {
			for(Integer id : istream.getNewPlayers()) {
				players.add(new DeadShip(CircleSpaceShip.create(spaceWorld, id, destroypool, false).setAttackMode()));
			}
		}


		for (int i = 0; i < players.size(); i++) {
			SpaceShip player = this.players.get(i);
			
			if (istream.hasRemovePlayerEvent(player)) {
				destroypool.add(player);
				players.remove(i);
				i--;
				continue;
			}
			
			player.autoheal();
		
			if (istream.getMouseMoveEvent(player) != null) {
				float x = istream.getMouseMoveEvent(player)[0];
				float y = istream.getMouseMoveEvent(player)[1];
				player.setMousePosition(new Vec2(x,y));
			}
			
			if (player.getCurrentEnergy() <= 0 && !(player instanceof DeadShip)) {
				System.out.println("DeadShip!!");
				players.remove(player);
				players.add(i,new DeadShip(player));
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
			
			Object[] event = istream.getBeBornEvent(player);
			
			if ( event != null) {
				String name = (String) event[1];
				if(player instanceof DeadShip){
					players.remove(i);
					players.add(i,((DeadShip)player).reborn(name));
					System.out.println("BornShip!!");

				}

			}
		}	
		
		istream.eraseNewPlayersEvents();
		istream.eraseRemovePlayersEvents();
		istream.eraseBeBornPlayersEvents();

	}
	
	public class RankingComparator implements Comparator<SpaceShip>{
		
	    public int compare(SpaceShip object1, SpaceShip object2) {
	        if(object1.getPoints() > object2.getPoints())
	        	return 1 ;
	        else if(object1.getPoints() == object2.getPoints())
	        	return 0 ;
	        else
	        	return -1 ;
	    }
	}
	
	public void afterStep(){
		Collections.sort(players, new RankingComparator());
		
		ostream.streamGame(spaceWorld,bots,players);
		destroypool.destroyAll();
	}
 
}
