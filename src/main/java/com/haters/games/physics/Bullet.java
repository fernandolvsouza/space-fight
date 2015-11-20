package com.haters.games.physics;

import org.jbox2d.dynamics.Body;

/**
 * Created by flvs on 11/2/15.
 */
public interface Bullet extends Destroyable, GameEntity, GameSerializable {

    void fire();

    void destroyCascade();

    int getDamage();

    Body getBody();

    int getId();

    float getAngle();

    SpaceShip getShip();

    Bullet withSpeed(float s);

    Bullet withRadius(float r);

    Bullet withDensity(float d);

    Bullet ready();
}
