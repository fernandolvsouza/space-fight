package com.haters.games.physics;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.haters.games.Group;
import org.jbox2d.common.Vec2;

import com.haters.games.input.GameInputStream;
import com.haters.games.output.NetworkOutputStream;

/**
 * @author Fernando Valente
 */

public class GameLogic {

	private static final int numberOfBots = 200;
	private static final int numberOfStars = 20;
	private static final int numberOfBases = 0;
	private static final int numberOfGroups = 4;

	private static final int ranksize = 10;
	private static final long spawnbotsfrequency = 120000;

	private static final long rakingfrequency = 2000;
	private long lastspawntime = 0;
	private long lastranktime = 0;
	
	private final List<SpaceShip> bots = new ArrayList<SpaceShip>();
	private final DestroyPool destroypool = new DestroyPool();
	
	private SpaceWorld spaceWorld;
	private List<Group> groups = new ArrayList<Group>(numberOfGroups);

	private List<SpaceShip> players = new ArrayList<SpaceShip>(1000);
	private List<Star> stars = new ArrayList<Star>(numberOfStars);
	private List<Base> bases = new ArrayList<Base>(numberOfBases);
	private GameInputStream istream;
	private NetworkOutputStream ostream;

	private long[] 	rankingIds = new long[ranksize];
	private Random rand = new Random();

	private final Group botGroup = new Group(GroupColor.YELLOW);

	

	
	public GameLogic(SpaceWorld spaceWorld, GameInputStream stream, NetworkOutputStream outputStream) {
		this.spaceWorld = spaceWorld;
		this.istream = stream;
		this.ostream = outputStream;
	}

	public void init() {

		spaceWorld.setup();
		spaceWorld.getWorld().setContactListener(new CollisionCallback(destroypool));
		for(int i=0;i< numberOfStars;i++){
			stars.add(new Star(spaceWorld));
		}

		for(int i=0;i<numberOfBases;i++){
			bases.add(new Base(spaceWorld));
		}

		groups.add(botGroup);

		for(int i=1;i<numberOfGroups;i++){
			if(i < GroupColor.values().length){
				groups.add(new Group(GroupColor.values()[i]));
			}
		}
		
	}

	private Color randomColor(){
		// Will produce only bright / light colours:
		float r = rand.nextFloat() / 2f + 0.5f;
		float g = rand.nextFloat() / 2f + 0.5f;
		float b = rand.nextFloat() / 2f + 0.5f;
		return new Color(r,g,b);
	}

	private Group smallerGroup(){
		Group min = null;
		for(Group group : groups){
			if(min== null || group.getMembers().size() < min.getMembers().size())
				min = group;
		}

		return min;
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		spaceWorld.step(f, velocityIterations, positionIterations);

		for (int i = 0; i < bases.size(); i++) {
			if (bases.get(i).getCurrentLife() <= 0) {
				System.out.println("DeadBase!!");
				destroypool.add(bases.get(i));
				bases.remove(i);
				i--;
			}
		}

		
		long now = new Date().getTime();
		if(now - lastspawntime > spawnbotsfrequency){
			int createbots = numberOfBots-bots.size();
			for(int i=0;i<createbots;i++){
				SpaceShip bot = CircleSpaceShip.createBot(spaceWorld, spaceWorld.getRandomPosition(), Sequence.getSequence(), destroypool).group(botGroup).setCruiseMode();
				bots.add(bot);			
			}
			lastspawntime = now;
		}
		
		for (int i = 0; i < bots.size(); i++) {
			SpaceShip bot = bots.get(i);			
			
			if(bot.getCurrentLife() <= 0){
				destroypool.add(bot);
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
				bot.fire(WeaponType.FAST_BULLET);
			}

			bot.up();
		}

		if (istream.hasNewPlayerEvent()) {
			for(Integer id : istream.getNewPlayers()) {
				players.add(new DeadShip(CircleSpaceShip.createPlayer(spaceWorld, spaceWorld.getRandomPosition(), id, destroypool).group(smallerGroup()).setAttackMode()));
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
			
			if (player.getCurrentLife() <= 0 && !(player instanceof DeadShip)) {
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
				player.fire(WeaponType.CHASE_BULLET);
			}

			if (istream.hasTryCaptureStarEvent(player)) { //'s'
				player.fire(WeaponType.STAR_CAPTURE_BULLET);
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
	        	return -1 ;
	        else if(object1.getPoints() == object2.getPoints())
	        	return 0 ;
	        else
	        	return 1 ;
	    }
	}
	
	public void afterStep(){
		Collections.sort(players, new RankingComparator());

		boolean sendRanking = false;

		for(int i=0;i<Math.min(ranksize,players.size());i++){
			if(players.get(i).getId() != rankingIds[i]){
				System.out.println(rankingIds[i]);
				rankingIds[i] = players.get(i).getId();
				sendRanking = true;
			}
		}
		long now = new Date().getTime();
		if(sendRanking || now - lastranktime > rakingfrequency){
			lastranktime = now;
			sendRanking = true;
		}

		ostream.streamGame(spaceWorld,bots,players,groups,stars,sendRanking,ranksize);

 		destroypool.destroyAll();
	}
 
}
