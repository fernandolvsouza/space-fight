package com.haters.games.physics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import sun.security.x509.DeltaCRLIndicatorExtension;

public class Plane {

	
	private int id;
	private Body body;
	private World world;
	private float maxAngularVelocity = 10.0f;
	
	private float angularDamping = 5.0f;
	private float maxAngularImpulse = 2.0f;
	
	private int maximumActiveBullets = 30;
	private long lastFireTime = 0;
	
	private int totalEnergy = 100;
	private int currentEnergy = totalEnergy;
	
	private List<Bullet> bullets = new ArrayList<Bullet>(this.maximumActiveBullets);;

	static public Plane create(World world,int id){
		return new Plane(world,id);
	}
	static public Plane create(World world, Vec2 pos, int id){
		return new Plane(world,pos,id);
	}
	
	private  Plane(World world,int id) {
		this.id = id;
		this.world = world;
		init(new Vec2(0,10));
	}
	
	private  Plane(World world,Vec2 pos, int id) {
		this.id = id;
		this.world = world;
		init(pos);
	}

	public Body getBody() {
		return body;
	}

	private void init(Vec2 pos) {
		// body definition
		BodyDef bd = new BodyDef();
		bd.setType(BodyType.DYNAMIC);
		bd.angularDamping = this.angularDamping;
		bd.linearDamping = 1.0f;

		// shape definition
		PolygonShape shape1 = new PolygonShape();

		Vec2[] vertices = new Vec2[3];
		vertices[0] = new Vec2(-1,1);
		vertices[1] = new Vec2(-1,-1);
		vertices[2] = new Vec2(3,0);

		shape1.set(vertices, 3);

		// shape definition
		PolygonShape shape2 = new PolygonShape();

		Vec2[] vertices2 = new Vec2[3];
		vertices2[0] = new Vec2(-1,2);
		vertices2[1] = new Vec2(-1,-2);
		vertices2[2] = new Vec2(1,0);

		shape2.set(vertices2, 3);

		// fixture definition
		FixtureDef fd = new FixtureDef();
		fd.shape = shape1;
		fd.density = 1;
		
		FixtureDef fd2 = new FixtureDef();
		fd2.shape = shape2;
		fd2.density = 1;

		// create dynamic body
		bd.setPosition(pos);
		this.body = this.world.createBody(bd);
		this.body.createFixture(fd);
		this.body.createFixture(fd2);

		this.body.setTransform(this.body.getPosition(),90 * MathUtils.DEG2RAD);
		this.body.setUserData(this);
	}

	public void fire() {
		Bullet bullet = getFirstInactiveOrActiveForTheLongestTimeBullet();
		long now = new Date().getTime();
		if( now - lastFireTime > bullet.getFireFrequency()) {
			bullet.fire();
			lastFireTime = now;
		}
	}
	
	private Bullet getFirstInactiveOrActiveForTheLongestTimeBullet(){
		if(bullets.size() < this.maximumActiveBullets){
			this.bullets.add(Bullet.create(this));
			return this.bullets.get(this.bullets.size()-1); 
		}
		
		Bullet activeForTheLongestTime = null;
		
		for (Bullet bullet : bullets) {
			if(activeForTheLongestTime == null || (activeForTheLongestTime != null && bullet.getActivationTime() < activeForTheLongestTime.getActivationTime())){
				activeForTheLongestTime = bullet;
			}
			if(bullet.inactive()){
				return bullet;
			}
		}
		return activeForTheLongestTime;		
	}
	
	public void turn(TurnState state) {
		float impulse;
		if (state == TurnState.LEFT) {
			impulse = (1 - (this.body.getAngularVelocity() / maxAngularVelocity)) * this.maxAngularImpulse;
		} else if (state == TurnState.RIGHT) {
			impulse = ((this.body.getAngularVelocity() / -maxAngularVelocity) - 1) * this.maxAngularImpulse;
		} else {
			impulse = 0;
		}
		this.body.applyAngularImpulse(impulse);
	}
	
	public void rotateToEnemy(Plane enemy, DebugDraw debugDraw) {
		
		Vec2 vectorToEnemy = new  Vec2(enemy.getBody().getPosition().x-this.body.getPosition().x,enemy.getBody().getPosition().y- this.body.getPosition().y);
			
		float angle = MathUtils.atan2(vectorToEnemy.y, vectorToEnemy.x);
		if (angle < 0) angle += 2 * MathUtils.PI;
		
		//debugDraw.drawSegment(enemy.getBody().getPosition(), this.body.getPosition() , Color3f.GREEN);

		float desiredAngle = angle;
		//System.out.println("desiredAngle: " + desiredAngle * MathUtils.RAD2DEG + ":: angle:" + this.body.getAngle());
		
		float nextAngle = this.body.getAngle() + this.body.getAngularVelocity() / 60.0f;
		
		float totalRotation = desiredAngle - nextAngle;
		while ( totalRotation < -180 * MathUtils.DEG2RAD ) totalRotation += 360 * MathUtils.DEG2RAD;
		while ( totalRotation >  180 * MathUtils.DEG2RAD ) totalRotation -= 360 * MathUtils.DEG2RAD;
		float desiredAngularVelocity = totalRotation * 60;
		float change = 10 * MathUtils.DEG2RAD; //allow 10 degree rotation per time step
		desiredAngularVelocity = MathUtils.min( change, MathUtils.max(-change, desiredAngularVelocity));
		
		float impulse = this.body.getInertia() * desiredAngularVelocity;// disregard time factor
		this.body.applyAngularImpulse(impulse);
	}
	
	private float angleBetweenToVector(Vec2 v1, Vec2 v2){		
		float angle = MathUtils.atan2(v2.y, v2.x) - MathUtils.atan2(v1.y, v1.x);
		if (angle < 0) angle += 2 * MathUtils.PI;
		return angle;
		
	}
	
	public boolean shouldFire(Plane enemy){
		Vec2 vectorToEnemy = new  Vec2(enemy.getBody().getPosition().x-this.body.getPosition().x,enemy.getBody().getPosition().y- this.body.getPosition().y);
		Vec2 direction = this.body.getWorldVector(new Vec2(1, 0));
		float angle = angleBetweenToVector(vectorToEnemy,direction);
		return MathUtils.abs(angle) < 30 *MathUtils.DEG2RAD;
	}

	public void accelerate(AccelerationState state) {
		
		float impulse = 0;
		
		Vec2 direction = this.body.getWorldVector(new Vec2(1, 0));
		direction.normalize();
		
		if (state == AccelerationState.UP) {
			impulse = +5;
		}else if(state == AccelerationState.DOWN){
			impulse = -5;
		}
		this.body.applyLinearImpulse(new Vec2(direction.x * impulse, direction.y * impulse), this.body.getPosition(), true);
	}

	public World getWorld() {
		return this.world;
	}

	public List<Bullet> getBullets() {
		return this.bullets;
	}
	public QueryCallback detectEnemy() {
		float detectRange = 40;
		AABB aabb = new AABB();
		aabb.lowerBound.set(new Vec2(this.body.getPosition().x - detectRange,this.body.getPosition().y - detectRange));
		aabb.upperBound.set(new Vec2(this.body.getPosition().x + detectRange,this.body.getPosition().y + detectRange));
		QueryCallback callback = new DetectEnemiesCallback(this,aabb);
		this.world.queryAABB(callback, aabb);
		return callback;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.id == ((Plane)obj).id;
	}
	
	@Override
	public String toString() {
		return "plane : {id: "+id+"}";
	}
	public void damage() {
		this.currentEnergy -= 5;
	}
	public int getCurrentEnergy(){
		return this.currentEnergy;
	}
	public void destroy() {
		this.getWorld().destroyBody(this.body);
	}
}
