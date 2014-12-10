package com.haters.games.physics;

import java.util.Random;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.haters.games.render.swing.DebugDrawJ2D;

public class SpaceWorld  {
	private World world;
	private float width = 150;

	public SpaceWorld(World world) {
		this.world = world;
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
	    
		
		// body definition
		BodyDef bd = new BodyDef();
		bd.setType(BodyType.STATIC);
		bd.setPosition(new Vec2(0, 0));

		// shape definition
		PolygonShape shape = new PolygonShape();

		// fixture definition
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1;
		
		//body
		Body boundaries = this.world.createBody(bd);		
		
		//add four walls to the static body
		shape.setAsBox(width/2, 1, new Vec2(0, 0), 0);
		boundaries.createFixture(fd);
				
		shape.setAsBox(1, width/2, new Vec2(-width/2, width/2), 0);
		boundaries.createFixture(fd);
		
		shape.setAsBox(width/2, 1, new Vec2(0, width), 0);
		boundaries.createFixture(fd);
		
		shape.setAsBox(1, width/2, new Vec2(width/2, width/2), 0);
		boundaries.createFixture(fd);	
	  
	}

	public Vec2 getRandomPosition() {
		return new Vec2(randomBetween(-width/2, width/2), randomBetween(0, width));
	}
	
	private float randomBetween(float x,float y){
		Random r = new Random();
		return (r.nextFloat() * (y-x)) + x;
	}

	public void step(float f, int velocityIterations, int positionIterations) {
		world.step(f, velocityIterations, positionIterations);	
	}

	public void setDebugDraw(DebugDrawJ2D debugDraw) {
		world.setDebugDraw(debugDraw);		
	}

	public void drawDebugData() {
		world.drawDebugData();
	}

}
