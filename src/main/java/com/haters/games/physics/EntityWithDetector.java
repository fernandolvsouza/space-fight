package com.haters.games.physics;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

/**
 * Created by flvs on 12/8/15.
 */
public interface EntityWithDetector {
    DetectEntitiesCallback getDetectionCallback();

    Body getBody();

    World getWorld();

    float getEnemyDetectRange();


    void detectGameEntities();
}
