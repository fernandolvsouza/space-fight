package com.haters.games.physics;

import java.util.Random;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import com.haters.games.render.swing.DebugDrawJ2D;

public class SpaceWorld  {
	private World world;
	private int radius = 500;
	private Boundaries boundaries;
	private DebugDrawJ2D debugDraw;

	public SpaceWorld(World world, DebugDrawJ2D debugDraw) {
		this.world = world;
		world.setDebugDraw(debugDraw);	
		this.debugDraw = debugDraw;
	}
	
	public World getWorld(){
		return this.world;
	}
	
	public void setup() {
		
		this.world.setGravity(new Vec2(0.0f, 0.0f));
	    world.setAllowSleep(true);
	    world.setWarmStarting(true);
	    world.setSubStepping(false);
	    world.setContinuousPhysics(true);
	    
	    boundaries = new Boundaries(this);
	    boundaries.setup();
	}

	public Vec2 getRandomPosition() {
		return new Vec2(randomBetween(-getRadius()/4, getRadius()/4), randomBetween(0, getRadius()/2));
	}
	
	private float randomBetween(float x,float y){
		Random r = new Random();
		return (r.nextFloat() * (y-x)) + x;
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		world.step(f, velocityIterations, positionIterations);	
	}
	
	public DebugDraw getDebugDraw() {
		return this.debugDraw;	
	}

	public void drawDebugData() {
		world.drawDebugData();
	}

	public int getRadius() {
		return radius;
	}

	public Boundaries getBoundaries() {
		return boundaries;
	}


}
