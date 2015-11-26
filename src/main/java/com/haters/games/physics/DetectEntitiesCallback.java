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
	public Set<Star> stars;
	public Set<Base> bases;
	public Boundaries boundaries;
	public List<Fixture> othersFixtures;

	
	public SpaceShip plane;

	public DetectEntitiesCallback(SpaceShip plane) {
		entities = new HashSet<GameEntity>();
		planes = new HashSet<SpaceShip>();
		bullets = new HashSet<Bullet>();
		stars = new HashSet<Star>();
		bases = new HashSet<Base>();
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
		}
		
		if (userdata instanceof Star) {
			stars.add((Star) userdata);
		}
		
		if (userdata instanceof SimpleBullet) {
			bullets.add((SimpleBullet) userdata);
		}

		if (userdata instanceof Boundaries) {
			boundaries = (Boundaries) userdata;
		}

		if (userdata instanceof GameEntity) {
			entities.add((GameEntity) userdata);
		}

		if (userdata instanceof Base) {
			bases.add((Base) userdata);
		}

		othersFixtures.add(fix);

		return true;
	}
	
	public void reset(){

		entities.clear();
		planes.clear();
		stars.clear();
		bullets.clear();
		bases.clear();
		othersFixtures.clear();
		boundaries = null;
	}

}