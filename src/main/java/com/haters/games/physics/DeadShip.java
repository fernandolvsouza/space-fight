package com.haters.games.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import com.haters.games.output.SERIALIZER_TYPE;

public class DeadShip implements SpaceShip{

	private SpaceShip deadplayer; 
	public DeadShip(SpaceShip player){
		deadplayer = player;
		deadplayer.getBody().getFixtureList().setSensor(true);
		deadplayer.getBody().setUserData(this);
	}
			
	@Override
	public int getId() {
		return deadplayer.getId();
	}

	@Override
	public Body getBody() {
		return deadplayer.getBody();
	}

	@Override
	public World getWorld() {
		return deadplayer.getWorld();
	}

	@Override
	public SERIALIZER_TYPE getType() {
		return SERIALIZER_TYPE.DEAD;
	}

	@Override
	public float getAngle() {
		return 0;
	}

	@Override
	public Vec2 getMousePosition() {
		return new Vec2();
	}

	@Override
	public int getCurrentEnergy() {
		return 0;
	}

	@Override
	public int getTotalEnergy() {
		return deadplayer.getTotalEnergy();
	}

	@Override
	public Set<SpaceShip> getShipsInRange() {
		return deadplayer.getShipsInRange();
	}

	@Override
	public Set<Bullet> getBulletsInRange() {
		return deadplayer.getBulletsInRange();
	}

	@Override
	public List<Bullet> getBullets() {
		return new ArrayList<Bullet>();
	}

	@Override
	public boolean isbot() {
		return deadplayer.isbot();
	}

	@Override
	public boolean isDamaged() {
		return false;
	}

	@Override
	public void setMousePosition(Vec2 to) {
	}

	@Override
	public void fire() {		
	}

	@Override
	public void autoheal() {
	}

	@Override
	public void detectGameEntities() {
		deadplayer.detectGameEntities();
	}

	@Override
	public void left() {
	}

	@Override
	public void right() {
	}

	@Override
	public void up() {
	}

	@Override
	public void down() {
	}

	@Override
	public void rotateTo(Vec2 position) {
	}

	@Override
	public boolean avoidColision() {
		return false;
	}

	@Override
	public boolean shouldFire(Set<SpaceShip> enemies) {
		return false;
	}

	@Override
	public void setReadyToDestroy(boolean b) {
		deadplayer.setReadyToDestroy(b);
	}

	@Override
	public boolean readyToDestroy() {
		return deadplayer.readyToDestroy();
	}

	@Override
	public void destroy() {
		deadplayer.destroy();
	}

	@Override
	public Set<SpaceShip> getAlivePlayersInRange() {

		return deadplayer.getAlivePlayersInRange();
	}

	@Override
	public Set<Garbage> getGarbagesInRange() {
		return deadplayer.getGarbagesInRange();
	}

	public SpaceShip reborn() {
		deadplayer.getBody().setTransform(deadplayer.getSpaceWorld().getRandomPosition(), deadplayer.getBody().getAngle());
		deadplayer.restoreEnergy();
		deadplayer.getBody().getFixtureList().setSensor(false);
		deadplayer.getBody().setUserData(deadplayer);
		return deadplayer;
	}
	
	@Override
    public int hashCode(){
		return deadplayer.hashCode();
	}
    
	@Override
    public boolean equals(Object obj){
		return deadplayer.equals(obj);
	}

	@Override
	public SpaceWorld getSpaceWorld() {
		return deadplayer.getSpaceWorld();
	}

	@Override
	public void restoreEnergy() {
	}
}
