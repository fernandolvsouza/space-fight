package com.haters.games.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

public class DeadShip implements SpaceShip, Destroyable,GameEntity{

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
	public String getType() {
		return "dead";
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
		((Destroyable) deadplayer).setReadyToDestroy(b);
	}

	@Override
	public boolean readyToDestroy() {
		return ((Destroyable) deadplayer).readyToDestroy();
	}

	@Override
	public void destroy() {
		((Destroyable) deadplayer).destroy();		
	}

	@Override
	public Set<SpaceShip> getAlivePlayersInRange() {

		return deadplayer.getAlivePlayersInRange();
	}

	@Override
	public Set<Garbage> getGarbagesInRange() {
		return deadplayer.getGarbagesInRange();
	}
}
