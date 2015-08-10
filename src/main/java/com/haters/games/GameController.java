package com.haters.games;


import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import com.haters.games.input.GameInputStream;
import com.haters.games.input.NetworkInputStream;
import com.haters.games.output.NetworkOutputStream;

import com.haters.games.physics.GameLogic;
import com.haters.games.physics.SpaceWorld;



public class GameController implements Runnable {


	private  static final int DEFAULT_FPS = 60;
	private static final int PositionIterations = 3;
	private static final int VelocityIterations = 8;

	private IViewportTransform transform = null;
	private final Vec2 initPosition = new Vec2(0,0);
	private final float initScale = 7;

	private Thread animation;

	public GameController() {
		super();
		this.animation = new Thread(this, "thread-1");
	}

	@Override
	public void run() {
		
		final SpaceWorld spaceWorld = new SpaceWorld(new World(new Vec2(0.0f, 0.0f)));
		final GameInputStream istream = new NetworkInputStream();
		
		GameLogic logic = new GameLogic(spaceWorld,this, istream, new NetworkOutputStream());
		logic.init();
				
		long beforeTime, afterTime, updateTime, timeDiff, sleepTime, timeSpent;
		float frameRate;
		int targetFrameRate;
		frameRate = targetFrameRate = DEFAULT_FPS;
	    float timeInSecs;

		beforeTime = updateTime = System.nanoTime();
	    sleepTime = 0;
	    
		while (true) {
			//engine.grabFocus();
			timeSpent = beforeTime - updateTime;
			if (timeSpent > 0) {
				timeInSecs = timeSpent * 1.0f / 1000000000.0f;
				updateTime = System.nanoTime();
				frameRate = (frameRate * 0.9f) + (1.0f / timeInSecs) * 0.1f;
			} else {
				updateTime = System.nanoTime();
			}

			istream.processEvents();
			logic.step(1f / frameRate, VelocityIterations, PositionIterations);
			logic.afterStep();

			
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
			//System.out.println("frame rate: " + frameRate);
			
		}
	}

	public void start() {
		animation.start();
	}

}
