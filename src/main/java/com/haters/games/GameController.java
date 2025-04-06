package com.haters.games;

import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haters.games.input.GameInputStream;
import com.haters.games.input.NetworkInputStream;
import com.haters.games.output.NetworkOutputStream;
import com.haters.games.physics.GameLogic;
import com.haters.games.physics.SpaceWorld;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private static final int DEFAULT_FPS = 60;
    private static final int POSITION_ITERATIONS = 3;
    private static final int VELOCITY_ITERATIONS = 8;

    private final IViewportTransform transform = null;
    private final Vec2 initPosition = new Vec2(0, 0);
    private final float initScale = 7;
    private float frameRate;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Thread animation;

    public GameController() {
        this.animation = new Thread(this, "game-loop");
    }

    public float getFps() {
        return frameRate;
    }

    public void stop() {
        running.set(false);
        animation.interrupt();
    }

    @Override
    public void run() {
        long lastPrintTime = 0;

        final SpaceWorld spaceWorld = new SpaceWorld(this, new World(new Vec2(0.0f, 0.0f)));
        final GameInputStream istream = new NetworkInputStream();
        GameLogic logic = new GameLogic(spaceWorld, istream, new NetworkOutputStream());
        logic.init();

        long beforeTime, afterTime, updateTime, timeDiff, sleepTime, timeSpent;
        int targetFrameRate;
        frameRate = targetFrameRate = DEFAULT_FPS;
        float timeInSecs;

        beforeTime = updateTime = System.nanoTime();
        sleepTime = 0;

        logger.info("Game loop started");

        while (running.get()) {
            timeSpent = beforeTime - updateTime;
            if (timeSpent > 0) {
                timeInSecs = timeSpent * 1.0f / 1000000000.0f;
                updateTime = System.nanoTime();
                frameRate = (frameRate * 0.9f) + (1.0f / timeInSecs) * 0.1f;
            } else {
                updateTime = System.nanoTime();
            }

            try {
                istream.processEvents();
                logic.step(1f / frameRate, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
                logic.afterStep();

                afterTime = System.nanoTime();
                timeDiff = afterTime - beforeTime;
                sleepTime = (1000000000 / targetFrameRate - timeDiff) / 1000000;
                
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }

                beforeTime = System.nanoTime();

                if (Instant.now().toEpochMilli() - lastPrintTime > 3000) {
                    logger.debug("Current frame rate: {}", frameRate);
                    lastPrintTime = Instant.now().toEpochMilli();
                }
            } catch (InterruptedException ex) {
                logger.warn("Game loop interrupted", ex);
                break;
            } catch (Exception ex) {
                logger.error("Error in game loop", ex);
            }
        }

        logger.info("Game loop stopped");
    }

    public void start() {
        animation.start();
    }
}
