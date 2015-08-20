package com.haters.games.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.List;

public class CollisionCallback implements ContactListener {

	private DestroyPool destroyPool;
	
	public CollisionCallback(DestroyPool destroyPool) {
		super();
		this.destroyPool = destroyPool;
	}

	@Override
	public void beginContact(Contact contact) {
		Object dataA = contact.getFixtureA().getBody().getUserData();
		Object dataB = contact.getFixtureB().getBody().getUserData();
	
		if(contactType(dataA,dataB,Bullet.class,CircleSpaceShip.class)){
			CircleSpaceShip plane = (dataA instanceof CircleSpaceShip ? (CircleSpaceShip)dataA : (CircleSpaceShip)dataB);
			Bullet bullet = (dataA instanceof Bullet ? (Bullet)dataA : (Bullet)dataB);
			destroyPool.add(bullet);
			plane.damage(bullet);
			
		}else if(contactType(dataA,dataB,Bullet.class,PolygonSpaceShip.class)){
			PolygonSpaceShip plane = (dataA instanceof PolygonSpaceShip ? (PolygonSpaceShip)dataA : (PolygonSpaceShip)dataB);
			Bullet bullet = (dataA instanceof Bullet ? (Bullet)dataA : (Bullet)dataB);
			destroyPool.add(bullet);
			plane.damage(bullet);
			
		}else if(contactType(dataA,dataB,Bullet.class,Bullet.class)){
			destroyPool.add((Bullet)dataA);
			destroyPool.add((Bullet)dataB);
		} else {
			if(dataA instanceof Bullet){
				destroyPool.add((Bullet)dataA);
			}
			if(dataB instanceof Bullet){
				destroyPool.add((Bullet)dataB);
			}
		}
	}

	private boolean contactType(Object dataA, Object dataB, Class<?>_class1 , Class<?> _class2) {
		//System.out.println("dataA:"+ dataA);
		//System.out.println("dataB:"+ dataB);
		//System.out.println("_class1:" + _class1);
		//System.out.println("_class2:" + _class2);
		if(dataA == null || dataB == null){
			return false;
		}
		return (dataA.getClass().equals(_class1) && dataB.getClass().equals(_class2)) 
					|| (dataA.getClass().equals(_class2) && dataB.getClass().equals(_class1))  ;
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preSolve(Contact contact, Manifold manifold) {
		// TODO Auto-generated method stub
	}
	
}