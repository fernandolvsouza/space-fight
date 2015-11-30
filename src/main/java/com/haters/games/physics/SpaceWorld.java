package com.haters.games.physics;

import com.haters.games.GameController;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.Random;



public class SpaceWorld  {
	private World world;
	private int radius = 500;
	private Boundaries boundaries;
	private GameController controller;

	public SpaceWorld(GameController gameController, World world) {
		this.world = world;
		this.controller = gameController;
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
		return new Vec2(randomBetween(-getRadius()/2, getRadius()/2), randomBetween(0, getRadius()));
	}

	public Vec2 getRandomPosition(int margin) {
		return new Vec2(randomBetween(-getRadius()/2 + margin, getRadius()/2-margin), randomBetween(0+margin, getRadius()-margin));
	}
	
	private float randomBetween(float x,float y){
		Random r = new Random();
		return (r.nextFloat() * (y-x)) + x;
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		world.step(f, velocityIterations, positionIterations);	
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


	public float getFps() {
		return controller.getFps();
	}
}
