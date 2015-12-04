package com.haters.games.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.haters.games.output.SERIALIZER_TYPE;


import java.util.Date;

public class SimpleBullet implements Bullet{

	public static final long FireFrequency = 300;
	private final int damage = 10;
	private float bulletspeed = 100.0f;
	private float radius = 0.2f;
	private float density = 0.2f;
	
	private Body body;
	private BodyDef bd;
	private FixtureDef fd;
	protected SpaceShip plane;
	private int sequence;

	
	private final long timeCreated = new Date().getTime();
	private boolean readyToDestroy = false;


	public SimpleBullet(SpaceShip plane) {
		this.plane = plane;
		this.sequence = Sequence.getSequence();
	}

	public Bullet withSpeed(float s){
		bulletspeed = s;
		return this;
	}

	public Bullet withRadius(float r){
		radius = r;
		return this;
	}

	public Bullet withDensity(float d){
		density = d;
		return this;
	}

	public Bullet ready() {

		// body definition
		bd = new BodyDef();
		bd.setType(BodyType.DYNAMIC);

		// shape definition
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);

		// fixture definition
		fd = new FixtureDef();
		fd.shape = shape;
		fd.density = density;
		fd.isSensor = true;

		return this;
	}



	@Override
	public void fire() {
		Vec2 center = plane.getBody().getWorldPoint(new Vec2(0, 0));
		Vec2 direction = plane.getBody().getWorldVector(new Vec2(1, 0));

		//float radius = ((CircleSpaceShip) plane).getRadius();
		float angle = MathUtils.atan2(direction.y,direction.x);


		Vec2 from = new Vec2(center.x + 2.5f*MathUtils.cos(angle),center.y + 2.5f*MathUtils.sin(angle));

		fireToDirection(from , direction);
	}

	protected void fireToDirection(Vec2 from, Vec2 direction) {
		direction.normalize();


		bd.setPosition(from);
		this.body = this.plane.getWorld().createBody(bd);
		this.body.createFixture(fd);

		this.body.setUserData(this);
		//this.body.m_linearDamping = 1.0f;

		float planeSpeed = plane.getBody().getLinearVelocity().length();

		//float velChange = bulletspeed - planeSpeed;
		//System.out.println(" velChange = " +  velChange);

		float impulse = body.getMass() * bulletspeed;

		this.body.applyLinearImpulse(new Vec2(direction.x * impulse, direction.y * impulse), this.body.getPosition(), true);
		//System.out.println("plane speed : " + planeSpeed);
		//System.out.println("bullet desired speed : " + bulletspeed);
		//System.out.println(" impulse = " +  impulse);
	}

	@Override
	public void destroy(){
		destroyCascade();
		this.plane.getBullets().remove(this);
	}

	@Override
	public void destroyCascade(){
		this.plane.getWorld().destroyBody(this.body);
	}

	@Override
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
		SimpleBullet other = (SimpleBullet) obj;
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

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public int getId() {
		return sequence;
	}

	@Override
	public float getAngle() {
		return MathUtils.atan2(this.body.getLinearVelocity().y,this.body.getLinearVelocity().x);
	}

	@Override
	public void setReadyToDestroy(boolean b){
		readyToDestroy  = b;
	}

	@Override
	public boolean readyToDestroy(){
		return readyToDestroy ;
	}

	@Override
	public SERIALIZER_TYPE getType(){
		return SERIALIZER_TYPE.BULLET;
	}

	@Override
	public SpaceShip getShip() {
		return this.plane;
	}
}