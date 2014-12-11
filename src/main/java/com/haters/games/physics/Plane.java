package com.haters.games.physics;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Plane implements Destroyable{

	private final static float maxAngularVelocity = 10.0f;
	private final static float angularDamping = 5.0f;
	private final static float maxAngularImpulse = 2.0f;
	private final static int maximumActiveBullets = 30;
	private final static int totalEnergy = 50;
	private final static float attackModeLinearDamping = 1.0f;
	private final static float cruiseModeLinearDamping = 3.0f;
	
	private int id;
	private Body body;
	private World world;
	
	private int currentEnergy = totalEnergy;
	private long lastFireTime = 0;
	private boolean isbot = true;
	
	private List<Destroyable> killthen;
	private LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	private DetectEnemiesCallback callback;

	static public Plane create(World world,int id, List<Destroyable> killthen, boolean isbot){
		return new Plane(world,id,killthen,isbot);
	}
	static public Plane create(World world, Vec2 pos, int id, List<Destroyable> killthen){
		return new Plane(world,pos,id,killthen);
	}
	
	private  Plane(World world,Vec2 pos, int id, List<Destroyable> killthen) {
		this.id = id;
		this.world = world;
		this.killthen = killthen; 
		init(pos);
	}
	
	private  Plane(World world,int id, List<Destroyable> killthen, boolean isbot) {
		this.id = id;
		this.world = world;
		this.killthen = killthen;
		this.isbot = isbot;
		init(new Vec2(0,10));
		
	}

	public Body getBody() {
		return body;
	}

	private void init(Vec2 pos) {
		// body definition
		BodyDef bd = new BodyDef();
		bd.setType(BodyType.DYNAMIC);
		bd.angularDamping = angularDamping;
		bd.linearDamping = cruiseModeLinearDamping;

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
		
		this.callback = new DetectEnemiesCallback(this);
	}

	public void fire() {
		
		long now = new Date().getTime();
		if( now - lastFireTime > Bullet.FireFrequency) {
			Bullet bullet = Bullet.create(this);
			this.bullets.add(bullet);
			bullet.fire();
			lastFireTime = now;
		}
		removeExcessBullets();
	}
	
	private void removeExcessBullets(){
		int wastebullets = bullets.size() - maximumActiveBullets;
		//System.out.println(this + "- bulletssize : " + bullets.size());
		if(wastebullets > 0){
			for (int i = 0; i < wastebullets; i++) {
				Bullet b = bullets.pop();
				//System.out.println(b + "- b.body : " + b.getBody());
				killthen.add(b);
			}
		}
	}
	
	public void turn(TurnState state) {
		float impulse;
		if (state == TurnState.LEFT) {
			impulse = (1 - (this.body.getAngularVelocity() / maxAngularVelocity)) * maxAngularImpulse;
		} else if (state == TurnState.RIGHT) {
			impulse = ((this.body.getAngularVelocity() / -maxAngularVelocity) - 1) * maxAngularImpulse;
		} else {
			impulse = 0;
		}
		this.body.applyAngularImpulse(impulse);
	}
	
	public void rotateToEnemy(Plane enemy) {
		
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
	
	public void avoidBodies() {
		// TODO Auto-generated method stub
		
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
	public void damage(Bullet b) {
		this.currentEnergy -= b.getDamage();
	}
	public int getCurrentEnergy(){
		return this.currentEnergy;
	}
	public void destroy() {
		for (Bullet bullet : this.bullets) {
			bullet.destroyCascade();
			bullet = null;
		}
		bullets.clear();
		this.getWorld().destroyBody(this.body);
	}

	public Plane detectEnemy() {
		this.callback.enemies.clear();
		float detectRange = 40;
		AABB aabb = new AABB();
		aabb.lowerBound.set(new Vec2(this.body.getPosition().x - detectRange,this.body.getPosition().y - detectRange));
		aabb.upperBound.set(new Vec2(this.body.getPosition().x + detectRange,this.body.getPosition().y + detectRange));
		this.world.queryAABB(callback, aabb);
		for (Plane enemy : this.callback.enemies) {
			if(!enemy.isbot()){
				return enemy;
			}
		}
		return null;
	}
	
	public boolean isbot(){
		return isbot;
	}
	
	public void setAttackMode() {
		this.body.m_linearDamping = attackModeLinearDamping;
	}
	public void setCruiseMode() {
		this.body.m_linearDamping = cruiseModeLinearDamping;		
	}
}
