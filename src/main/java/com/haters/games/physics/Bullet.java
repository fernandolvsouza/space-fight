package com.haters.games.physics;

import java.util.Date;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Bullet implements Destroyable, GameEntity{

	public static final long FireFrequency = 200;
	private static final int damage = 10;
	private static final float fireLinearImpulse = 10.0f;
	
	private Body body;
	private BodyDef bd;
	private FixtureDef fd;
	private SpaceShip plane;
	private int sequence;	
	
	private final long timeCreated = new Date().getTime();
	
	private Bullet(SpaceShip plane,int sequence) {
		this.plane = plane;
		this.sequence = sequence;
		init();
	}

	private void init() {
		// body definition
		bd = new BodyDef();
		bd.setType(BodyType.DYNAMIC);

		// shape definition
		CircleShape shape = new CircleShape();
		shape.setRadius(0.5f);

		// fixture definition
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 0.2f;
	}

	public static Bullet create(SpaceShip plane,int sequence) {
		return new Bullet(plane,sequence);
	}

	public void fire() {
		bd.setPosition(plane.getBody().getWorldPoint(new Vec2(4, 0)));
		this.body = this.plane.getWorld().createBody(bd);
		this.body.createFixture(fd);
		
		this.body.setUserData(this);
		
		Vec2 direction = plane.getBody().getWorldVector(new Vec2(1, 0));
		direction.normalize();
		
		float planeSpeed = plane.getBody().getLinearVelocity().length(); 
		
		this.body.applyLinearImpulse(new Vec2(direction.x * (fireLinearImpulse + planeSpeed), direction.y * (fireLinearImpulse + planeSpeed)), this.body.getPosition(), true);
		
	}
	
	public void destroy(){
		destroyCascade();
		this.plane.getBullets().remove(this);
	}
	
	public void destroyCascade(){
		this.plane.getWorld().destroyBody(this.body);
	}
	
	public int getDamage() {		
		return damage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (timeCreated ^ (timeCreated >>> 32));
		result = prime * result + ((plane == null) ? 0 : plane.hashCode());
		result = prime * result + sequence;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bullet other = (Bullet) obj;
		if (timeCreated != other.timeCreated)
			return false;
		if (plane == null) {
			if (other.plane != null)
				return false;
		} else if (!plane.equals(other.plane))
			return false;
		if (sequence != other.sequence)
			return false;
		return true;
	}

	public Body getBody() {
		return body;
	}
	
}