package com.haters.games.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CollisionCallback implements ContactListener {

	private DestroyPool destroyPool;
	
	public CollisionCallback(DestroyPool destroyPool) {
		super();
		this.destroyPool = destroyPool;
	}

	@Override
	public void beginContact(Contact contact) {

		boolean sensorA = contact.getFixtureA().isSensor();
		boolean sensorB = contact.getFixtureB().isSensor();

		Object dataA = contact.getFixtureA().getBody().getUserData();
		Object dataB = contact.getFixtureB().getBody().getUserData();

		//collision with star sensor, nothing happens
		if(dataA instanceof Star && sensorA)
			return;

		if(dataB instanceof Star && sensorB)
			return;


		//any other collision bullet must be destroyed
		if(dataA instanceof Bullet)
			destroyPool.add((SimpleBullet)dataA);
		if(dataB instanceof SimpleBullet)
			destroyPool.add((SimpleBullet)dataB);


		if(isDamageContact(dataA, dataB)) {
			LifePointsEntity entity = (dataA instanceof LifePointsEntity ? (LifePointsEntity)dataA : (LifePointsEntity)dataB);
			SimpleBullet bullet = (dataA instanceof SimpleBullet ? (SimpleBullet)dataA : (SimpleBullet)dataB);;
			entity.damage(bullet);

		} else if(contactType(dataA,dataB,StarCaptureBullet.class,Star.class)) {
			StarCaptureBullet starCaptureBullet = (dataA instanceof StarCaptureBullet ? (StarCaptureBullet)dataA : (StarCaptureBullet)dataB);
			Star star = (dataA instanceof Star ? (Star)dataA : (Star)dataB);
			starCaptureBullet.getShip().getGroup().takeStar(star);
		}
	}

	private boolean isDamageContact(Object dataA, Object dataB){
		if(dataA == null || dataB == null){
			return false;
		}

		return (dataA instanceof SimpleBullet && dataB instanceof LifePointsEntity)
				|| (dataA instanceof LifePointsEntity  && dataB instanceof SimpleBullet);
	}

	private boolean contactType(Object dataA, Object dataB, Class<?>_class1 , Class<?> _class2) {

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