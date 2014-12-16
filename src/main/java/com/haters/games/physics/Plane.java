package com.haters.games.physics;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


public class Plane implements Destroyable, GameEntity{

	private final static float maxAngularVelocity = 10.0f;
	private final static float angularDamping = 5.0f;
	private final static float botAngularDamping = 2.0f;
	private final static float maxAngularImpulse = 2.0f;
	private final static int maximumActiveBullets = 30;
	private final static int totalEnergy = 1000;
	private final static float attackModeLinearDamping = 1.0f;
	private final static float cruiseModeLinearDamping = 3.0f;
	private final static int enemyDetectRange = 40;
	
	private final long timeCreated = new Date().getTime();
	private int id;
	private int bulletsSequence = 0;
	private int currentEnergy = totalEnergy;
	private long lastFireTime = 0;
	private boolean isbot = true;
	
	private Body body;
	private World world;	
	private List<Destroyable> killthen;
	private LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	private DetectEntitiesCallback detectionCallback;

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
		bd.angularDamping = isbot ? botAngularDamping : angularDamping;
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
		
		this.detectionCallback = new DetectEntitiesCallback(this);
	}

	public void fire() {
		
		long now = new Date().getTime();
		if( now - lastFireTime > Bullet.FireFrequency) {
			Bullet bullet = Bullet.create(this,bulletsSequence++);
			this.bullets.add(bullet);
			bullet.fire();
			lastFireTime = now;
		}
		removeExcessBullets();
	}
	
	private void removeExcessBullets(){
		int wastebullets = bullets.size() - maximumActiveBullets;
		if(wastebullets > 0){
			for (int i = 0; i < wastebullets; i++) {
				Bullet b = bullets.pop();
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
	
	public void rotateTo(Vec2 point) {
		
		Vec2 vectorToPoint = new  Vec2(point.x-this.body.getPosition().x,point.y- this.body.getPosition().y);
			
		float angle = MathUtils.atan2(vectorToPoint.y, vectorToPoint.x);
		rotate(angle);
	}
	
	private void rotate(float angle) {
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
	public void rotateSin(){
		rotate(body.getPosition().x);
	}
	
	public boolean avoidColision(DebugDraw debugDraw) {
		float rayLength = 10;
		int[] angles = new int[10];
		for (int i=0;i<angles.length; i++) {
			angles[i] = i*360/angles.length;
		}	
		Vec2 sum = null;
		for(Fixture fix  : detectionCallback.othersFixtures){
			for(int i=0; i<angles.length;i++){
			
				float angle =  angles[i] * MathUtils.DEG2RAD;
				RayCastOutput output = new RayCastOutput();
				RayCastInput input = new RayCastInput();
				input.p1.x = this.getBody().getPosition().x;
				input.p1.y = this.getBody().getPosition().y;
				input.maxFraction = 1;
				
				Vec2 p2 = this.body.getWorldPoint( new Vec2(MathUtils.sin(angle),MathUtils.cos(angle)).mul(rayLength));
				input.p2.x = p2.x;
				input.p2.y = p2.y;
				//debugDraw.drawSegment(input.p1, input.p2, Color3f.BLUE);
				if(fix.raycast(output, input, 1)){
					Vec2  normal = output.normal.mul(rayLength);
					if(sum == null){
						sum = new Vec2();
					}
					sum = sum.add(normal);
				}
			}
		}
		if(sum != null){
			sum.normalize();
			
			Vec2 pointToGo = this.getBody().getPosition().add(sum.mul(rayLength));
			rotateTo(pointToGo);
			debugDraw.drawSegment(this.getBody().getPosition(), pointToGo, Color3f.RED);
			return false;
		}
		return true;
	}

	
	private float angleBetweenToVector(Vec2 v1, Vec2 v2){		
		float angle = MathUtils.atan2(v2.y, v2.x) - MathUtils.atan2(v1.y, v1.x);
		if (angle < 0) angle += 2 * MathUtils.PI;
		return angle;
		
	}
	
	public boolean shouldFire(Set<Plane> enemies){
		for (Plane enemy : enemies) {
			Vec2 vectorToEnemy = new  Vec2(enemy.getBody().getPosition().x-this.body.getPosition().x,enemy.getBody().getPosition().y- this.body.getPosition().y);
			Vec2 direction = this.body.getWorldVector(new Vec2(1, 0));
			float angle = angleBetweenToVector(vectorToEnemy,direction);
			return MathUtils.abs(angle) < 30 *MathUtils.DEG2RAD;
		}
		return false;
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

	public Set<Plane> getEnemiesInRange() {
		return this.detectionCallback.planes;
	}
	
	public Boundaries getBoundsInRange() {
		return this.detectionCallback.boundaries;
	}
		
	public void detectGameEntities() {
		detectionCallback.reset();
		AABB aabb = new AABB();
		aabb.lowerBound.set(new Vec2(this.body.getPosition().x - enemyDetectRange,this.body.getPosition().y - enemyDetectRange));
		aabb.upperBound.set(new Vec2(this.body.getPosition().x + enemyDetectRange,this.body.getPosition().y + enemyDetectRange));
		this.world.queryAABB(detectionCallback, aabb);
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (isbot ? 1231 : 1237);
		result = prime * result + (int) (timeCreated ^ (timeCreated >>> 32));
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
		Plane other = (Plane) obj;
		if (id != other.id)
			return false;
		if (isbot != other.isbot)
			return false;
		if (timeCreated != other.timeCreated)
			return false;
		return true;
	}
	
}
