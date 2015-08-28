package com.haters.games.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.List;
import java.util.Set;

/**
 * Created by flvs on 8/9/15.
 */
public interface SpaceShip  extends GameEntity,GameSerializable{

	//getters
	
    int getId();

    Body getBody();
    
    World getWorld();
    
    SpaceWorld getSpaceWorld();
        
    float getAngle();

	Vec2 getMousePosition();

    int getCurrentEnergy();
    
    int getTotalEnergy();
    
    Set<SpaceShip> getShipsInRange();
    
    Set<Bullet> getBulletsInRange();

    List<Bullet> getBullets();
    
    Set<SpaceShip> getAlivePlayersInRange();
    
    Set<Garbage> getGarbagesInRange();
    
    boolean isbot();
    
    boolean isDamaged();
    
    String getName();
    
    void setName(String name);
    
    //setter
    
    void setMousePosition(Vec2 to);
    
    
    //generic actions
    void fire();
    
    void autoheal();
    
	void restoreEnergy();

    void detectGameEntities();
   
    
    //moving methods
	
	void left();

	void right();

	void up();

	void down();

    void rotateTo(Vec2 position);
    
    //bot methods
    
    boolean avoidColision();

    boolean shouldFire(Set<SpaceShip> enemies);

    	    
}
