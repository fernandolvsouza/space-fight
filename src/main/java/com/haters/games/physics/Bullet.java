package com.haters.games.physics;

import java.util.Date;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Bullet {
	private Body body;
	private BodyDef bd;
	private FixtureDef fd;
	private Plane plane;
	
	private long activationTime = 0;
	private boolean active = false;
	private float fireLinearImpulse = 10.0f;
	private int fireFrequency = 200; // milliseconds
	
	private boolean readyToDestroy = false;
	
	private Bullet(Plane plane) {
		this.plane = plane;
		init();
	}

	private void init() {
		// body definition
		bd = new BodyDef();
		bd.setType(BodyType.DYNAMIC);

		// shape definition
		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);

		// fixture definition
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1;
	}

	public static Bullet create(Plane plane) {
		return new Bullet(plane);
	}

	public long getActivationTime() {
		return this.activationTime;
	}
	
	public long setActivationTime(long activationTime) {
		return this.activationTime = activationTime;
	}

	public boolean inactive() {
		return !active;
	}

	public void fire() {
		// create dynamic body
		if(this.body != null){
			this.destroy();
		}
		bd.setPosition(plane.getBody().getWorldPoint(new Vec2(4, 0)));
		this.body = this.plane.getWorld().createBody(bd);
		this.body.createFixture(fd);
		
		this.body.setUserData(this);
		
		Vec2 direction = plane.getBody().getWorldVector(new Vec2(1, 0));
		direction.normalize();
		
		float planeSpeed = plane.getBody().getLinearVelocity().length(); 
		
		this.body.applyLinearImpulse(new Vec2(direction.x * (fireLinearImpulse + planeSpeed), direction.y * (fireLinearImpulse + planeSpeed)), this.body.getPosition(), true);
		
		this.active = true;
		this.activationTime = new Date().getTime();
		
	}
	
	public void destroy(){
		this.plane.getWorld().destroyBody(this.body);
		this.active = false;
	}
	
	public int getFireFrequency(){
		return this.fireFrequency;
	}

	public void setReadyToDestroy() {
		this.readyToDestroy = true;
	}
	
	public boolean readyToDestroy() {
		return this.readyToDestroy;
	}
}