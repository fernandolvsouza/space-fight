package com.haters.games.physics;

/**
 * Created by flvs on 9/27/15.
 */
public interface LifePointsEntity {

    public int getCurrentLife();
    public int getTotalLife();
    boolean isDamaged();
    public void damage(Bullet b);
}
