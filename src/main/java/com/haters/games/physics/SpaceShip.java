package com.haters.games.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.List;
import java.util.Set;

/**
 * Created by flvs on 8/9/15.
 */
public interface SpaceShip {

    int getId();

    Body getBody();

    //void turn(TurnState left);

    //void accelerate(AccelerationState up);

    int getCurrentEnergy();

    void fire();

    void detectGameEntities();

    Set<SpaceShip> getShipsInRange();
    
    Set<Bullet> getBulletsInRange();

    List<Bullet> getBullets();

   boolean isbot();
    
    //bot methods
    boolean avoidColision();

    void rotateTo(Vec2 position);

    boolean shouldFire(Set<SpaceShip> enemies);

    World getWorld();

    String getType();
    
    float getAngle();

	void setMousePosition(Vec2 to);

	Vec2 getMousePosition();

	void left();

	void right();

	void up();

	void down();
    
}
