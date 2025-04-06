package com.haters.games;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.junit.Before;

/**
 * Base test class with common utilities for testing game components.
 */
public abstract class BaseTest {
    protected World world;
    protected static final float DELTA = 0.0001f;

    @Before
    public void setUp() {
        world = new World(new Vec2(0.0f, 0.0f));
    }

    protected Vec2 createVec2(float x, float y) {
        return new Vec2(x, y);
    }

    protected void stepWorld() {
        world.step(1.0f/60.0f, 8, 3);
    }
} 