package com.haters.games.physics;

import com.haters.games.Group;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.List;
import java.util.Set;

/**
 * Created by flvs on 8/9/15.
 */
public interface SpaceShip extends EntityWithDetector, LifePointsEntity, Destroyable, GameEntity, GameSerializable {

    //getters

    int getId();

    Body getBody();

    World getWorld();

    SpaceWorld getSpaceWorld();

    Group getGroup();

    float getAngle();

    Vec2 getMousePosition();

    int getCurrentLife();

    int getTotalLife();

    Set<SpaceShip> getShipsInRange();

    Set<Bullet> getBulletsInRange();

    List<Bullet> getBullets();

    Set<SpaceShip> getAlivePlayersInRange();

    Set<Star> getGarbagesInRange();

    Set<Base> getBasesInRange();

    boolean isbot();

    boolean isDamaged();

    String getName();

    int getStarCaptureBulletReloadPercentage();


    //setter
    void setName(String name);

    void setMousePosition(Vec2 to);

    SpaceShip setAttackMode();

    SpaceShip setCruiseMode();

    //generic actions
    void fire(BulletType type);

    void autoheal();

    void restoreEnergy();


    //moving methods

    void left();

    void right();

    void up();

    void down();

    void rotateTo(Vec2 position);

    //bot methods

    boolean avoidColision();

    boolean shouldFire(Set<SpaceShip> enemies);

    void addPoint(int i);

    void removePoint(int i);

    int getPoints();


    void powerUp();

    void powerDown();

    int getPower();

    void powerClear();
}
