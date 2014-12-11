package com.haters.games.physics;

import java.util.HashSet;
import java.util.Set;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Fixture;

public class DetectEnemiesCallback implements QueryCallback {

	public Set<Plane> enemies;
	public Plane plane;

	public DetectEnemiesCallback(Plane plane) {
		enemies = new HashSet<Plane>();
		this.plane = plane;
	}
	public boolean reportFixture(Fixture fix) {

		Object userdata = fix.getBody().getUserData();
		
		if (userdata instanceof Plane && !((Plane)userdata).equals(plane)) {
			enemies.add((Plane) userdata);
		}

		return true;
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