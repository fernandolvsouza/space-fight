package com.haters.games.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Fixture;


public class DetectEntitiesCallback implements QueryCallback {

	public Set<GameEntity> entities;
	public Set<SpaceShip> planes;
	public Set<Bullet> bullets;
	public Boundaries boundaries;
	public List<Fixture> boundFixtures;
	public List<Fixture> othersFixtures;
	
	public SpaceShip plane;

	public DetectEntitiesCallback(SpaceShip plane) {
		entities = new HashSet<GameEntity>();
		planes = new HashSet<SpaceShip>();
		bullets = new HashSet<Bullet>();
		boundFixtures = new ArrayList<Fixture>();
		othersFixtures = new ArrayList<Fixture>();
		this.plane = plane;
	}
	public boolean reportFixture(Fixture fix) {
		Object userdata = fix.getBody().getUserData();
		
		if (userdata.equals(plane)) {
			return true;
		}
		
		if (userdata instanceof SpaceShip ) {
			planes.add((SpaceShip) userdata);
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
		if(!plane.isbot()){
			System.out.println("Entities =  " + entities.size() + " , Planes = " + planes.size() + " , Bullets = " + bullets.size() + " , limits  = " + boundaries );
		}
		entities.clear();
		planes.clear();
		bullets.clear();
		boundFixtures.clear();
		othersFixtures.clear();
		boundaries = null;
	}
/*	public void debug(DebugDraw debugDraw) {
		Vec2[] debug = new Vec2[4];
		debug[0] = new Vec2(aabb.lowerBound.x, aabb.lowerBound.y);
		debug[1] = new Vec2(aabb.upperBound.x, aabb.lowerBound.y);
		debug[2] = new Vec2(aabb.upperBound.x, aabb.upperBound.y);
		debug[3] = new Vec2(aabb.lowerBound.x, aabb.upperBound.y);
		debugDraw.drawPolygon(debug, 4, Color3f.BLUE);
	}*/
}