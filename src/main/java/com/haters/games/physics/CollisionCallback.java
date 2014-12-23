package com.haters.games.physics;

import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CollisionCallback implements ContactListener {

	private List<Destroyable> killthen;
	
	public CollisionCallback(List<Destroyable> killthen) {
		super();
		this.killthen = killthen;
	}

	@Override
	public void beginContact(Contact contact) {
		Object dataA = contact.getFixtureA().getBody().getUserData();
		Object dataB = contact.getFixtureB().getBody().getUserData();
	
		if(contactType(dataA,dataB,Bullet.class,SpaceShip.class)){
			SpaceShip plane = (dataA instanceof SpaceShip ? (SpaceShip)dataA : (SpaceShip)dataB);
			Bullet bullet = (dataA instanceof Bullet ? (Bullet)dataA : (Bullet)dataB);
			killthen.add(bullet);
			plane.damage(bullet);
			
		}else if(contactType(dataA,dataB,Bullet.class,Bullet.class)){
			killthen.add((Bullet)dataA);
			killthen.add((Bullet)dataB);
		} else {
			if(dataA instanceof Bullet){
				killthen.add((Bullet)dataA);
			}
			if(dataB instanceof Bullet){
				killthen.add((Bullet)dataB);
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