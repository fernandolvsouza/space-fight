package com.haters.games.physics;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Fixture;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DetectEntitiesCallback implements QueryCallback {

	public Set<GameEntity> entities;
	public Set<SpaceShip> planes;
	public Set<Bullet> bullets;
	public Set<Energy> energies;
	public Boundaries boundaries;
	public List<Fixture> boundFixtures;
	public List<Fixture> othersFixtures;
	
	public SpaceShip plane;

	public DetectEntitiesCallback(SpaceShip plane) {
		entities = new HashSet<GameEntity>();
		planes = new HashSet<SpaceShip>();
		bullets = new HashSet<Bullet>();
		energies = new HashSet<Energy>();
		boundFixtures = new ArrayList<Fixture>();
		othersFixtures = new ArrayList<Fixture>();
		this.plane = plane;
	}
	public boolean reportFixture(Fixture fix) {
		Object userdata = fix.getBody().getUserData();
		
		if (userdata.equals(plane)) {
			return true;
		}
		
		if (userdata instanceof PolygonSpaceShip || userdata instanceof CircleSpaceShip) {
			planes.add((SpaceShip) userdata);
			othersFixtures.add(fix);
		}
		
		if (userdata instanceof Energy) {
			energies.add((Energy)userdata);
			othersFixtures.add(fix);
		}
		
		if (userdata instanceof Bullet) {
			bullets.add((Bullet) userdata);
		}
		
		if (userdata instanceof GameEntity) {
			entities.add((GameEntity) userdata);
		}
		
		if (userdata instanceof Boundaries) {
			boundaries = (Boundaries) userdata;
			boundFixtures.add(fix);
			othersFixtures.add(fix);
		}

		return true;
	}
	
	public void reset(){

		entities.clear();
		planes.clear();
		energies.clear();
		bullets.clear();
		boundFixtures.clear();
		othersFixtures.clear();
		boundaries = null;
	}

}