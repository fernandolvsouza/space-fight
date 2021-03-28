package com.haters.games.physics;

/**
 * Created by flvs on 9/27/15.
 */
public interface LifePointsEntity {

    int getCurrentLife();

    int getTotalLife();

    boolean isDamaged();

    void damage(SimpleBullet b);
}
