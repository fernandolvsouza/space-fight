package com.haters.games.physics;

/**
 * Created by flvs on 11/8/15.
 */
public class BulletFactory {
    public static Bullet create(BulletType type, SpaceShip spaceShip) {
        if(type.equals(type.SIMPLE_BULLET))
            return new SimpleBullet(spaceShip).ready();
        else if(type.equals(type.STAR_CAPTURE_BULLET))
            return new StarCaptureBullet(spaceShip).withSpeed(45.0f).withRadius(0.2f).withDensity(0.2f).ready();
        else if(type.equals(type.CHASE_BULLET))
            return new ChaseBullet(spaceShip).withSpeed(45.0f).withRadius(0.2f).withDensity(0.2f).ready();
        else
            return null;
    }
}
