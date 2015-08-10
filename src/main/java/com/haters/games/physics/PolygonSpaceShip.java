package com.haters.games.physics;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.shapes.PolygonShape;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;


public class PolygonSpaceShip extends BasicShip implements SpaceShip,Destroyable, GameEntity{

	private final static float maxAngularVelocity = 10.0f;
	private final static float angularDamping = 5.0f;
	private final static float botAngularDamping = 2.0f;
	private final static float maxAngularImpulse = 2.0f;

	private final static int totalEnergy = 100;
	private final static int enemyDetectRange = 70;



	static public PolygonSpaceShip create(World world,int id, List<Destroyable> killthen, boolean isbot){
		return new PolygonSpaceShip(world,id,killthen,isbot);
	}
	static public PolygonSpaceShip create(World world, Vec2 pos, int id, List<Destroyable> killthen){
		return new PolygonSpaceShip(world,pos,id,killthen);
	}

	private PolygonSpaceShip(World world, Vec2 pos, int id, List<Destroyable> killthen) {
		super(world,pos, id, killthen);
	}

	private PolygonSpaceShip(World world, int id, List<Destroyable> killthen, boolean isbot) {
		super(world,id,killthen,isbot);
	}

	protected void init(Vec2 pos) {
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

	@Override
	protected int getTotalEnergy() {
		return totalEnergy;
	}

	@Override
	protected float getEnemyDetectRange() {
		return enemyDetectRange;
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
	
	public boolean avoidColision() {
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
			return false;
		}
		return true;
	}

	
	public boolean shouldFire(Set<SpaceShip> enemies){
		for (SpaceShip enemy : enemies) {
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

}
