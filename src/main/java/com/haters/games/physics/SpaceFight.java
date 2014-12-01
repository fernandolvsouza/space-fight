package com.haters.games.physics;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import com.haters.games.render.RenderEngine;
import com.haters.games.render.swing.DebugDrawJ2D;


public class SpaceFight implements Runnable {


	private  static final int DEFAULT_FPS = 60;
	private static final int PositionIterations = 3;
	private static final int VelocityIterations = 8;
	
	private final IViewportTransform transform;
	private final Vec2 initPosition = new Vec2(0,20);
	private float initScale = 10;
	

	
	private RenderEngine engine;
	private DebugDrawJ2D debugDraw;
	
	
	
	public SpaceFight(RenderEngine engine,DebugDrawJ2D debugDraw) {
		super();
		this.engine = engine;
		this.debugDraw = debugDraw;
	    this.transform = new OBBViewportTransform();
	    transform.setCamera(initPosition.x, initPosition.y, initScale);
	    this.debugDraw.setViewportTransform(transform);
	    configureDebugDraw(this.debugDraw);
	}
	
	private void configureDebugDraw(DebugDrawJ2D debugDraw){
	    int flags = 0;
	    flags += DebugDraw.e_shapeBit; // disable 0
	    flags += 0; //DebugDraw.e_jointBit 
	    flags += DebugDraw.e_aabbBit; // disable 0
	    flags += 0;  //DebugDraw.e_centerOfMassBit
	    flags += 0; //DebugDraw.e_dynamicTreeBit
	    flags += 0;// DebugDraw.e_wireframeDrawingBit
	    debugDraw.setFlags(flags);
	}

	@Override
	public void run() {
		
		final SpaceWorld spaceWorld = new SpaceWorld(new World(new Vec2(0.0f, 0.0f)));
		spaceWorld.setDebugDraw(this.debugDraw);
		spaceWorld.setup();
				
		long beforeTime, afterTime, updateTime, timeDiff, sleepTime, timeSpent;
		float frameRate;
		int targetFrameRate;
		frameRate = targetFrameRate = DEFAULT_FPS;
	    float timeInSecs;

		beforeTime = updateTime = System.nanoTime();
	    sleepTime = 0;

		while (true) {
			timeSpent = beforeTime - updateTime;
			if (timeSpent > 0) {
				timeInSecs = timeSpent * 1.0f / 1000000000.0f;
				updateTime = System.nanoTime();
				frameRate = (frameRate * 0.9f) + (1.0f / timeInSecs) * 0.1f;
			} else {
				updateTime = System.nanoTime();
			}

			//render
			if(engine.render()){
				spaceWorld.step(1f / DEFAULT_FPS, VelocityIterations, PositionIterations);
				spaceWorld.drawDebugData();
				engine.paintScreen();
				System.out.println("Step!!");
			}
			
			
			afterTime = System.nanoTime();

			timeDiff = afterTime - beforeTime;
			sleepTime = (1000000000 / targetFrameRate - timeDiff) / 1000000;
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException ex) {
				}
			}

			beforeTime = System.nanoTime();
			System.out.println("frame rate: " + frameRate);
		}
	}

}
