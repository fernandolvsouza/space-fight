package com.haters.games.physics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.haters.games.output.SERIALIZER_TYPE;


public class PolygonSpaceShip extends BasicShip implements SpaceShip,Destroyable, GameEntity{

	private final static float maxAngularVelocity = 10.0f;
	private final static float angularDamping = 5.0f;
	private final static float botAngularDamping = 2.0f;
	private final static float maxAngularImpulse = 2.0f;

	private final static int totalEnergy = 10;
	private final static int enemyDetectRange = 70;



	static public PolygonSpaceShip create(SpaceWorld world,int id, DestroyPool killthen, boolean isbot){
		return new PolygonSpaceShip(world,id,killthen,isbot);
	}
	static public PolygonSpaceShip create(SpaceWorld world, Vec2 pos, int id, DestroyPool killthen){
		return new PolygonSpaceShip(world,pos,id,killthen);
	}

	private PolygonSpaceShip(SpaceWorld world, Vec2 pos, int id, DestroyPool killthen) {
		super(world,pos, id, killthen);
	}

	private PolygonSpaceShip(SpaceWorld world, int id, DestroyPool killthen, boolean isbot) {
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
		this.body = this.world.getWorld().createBody(bd);
		this.body.createFixture(fd);
		this.body.createFixture(fd2);

		this.body.setTransform(this.body.getPosition(),90 * MathUtils.DEG2RAD);
		this.body.setUserData(this);
		
		this.detectionCallback = new DetectEntitiesCallback(this);
	}

	@Override
	public int getTotalEnergy() {
		
		return isbot ? 10 : totalEnergy;
	}

	@Override
	protected float getEnemyDetectRange() {
		return enemyDetectRange;
	}


	private  void turn(TurnState state) {
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
	
	private  void accelerate(AccelerationState state) {
		
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
	
	
	@Override
	public SERIALIZER_TYPE getType() {
		return SERIALIZER_TYPE.POLYGON;
	}


	@Override
	public float getAngle() {
		return body.getAngle();
	}
	
	@Override
	public void setMousePosition(Vec2 mouse_pos) {
		this.mouse_pos = mouse_pos;
	}
	
	@Override
	public Vec2 getMousePosition() {
		return mouse_pos;
	}
	
	@Override
	public void left() {
		turn(TurnState.LEFT);
	}
	
	@Override
	public void right() {
		turn(TurnState.RIGHT);		
	}
	
	@Override
	public void up() {
		accelerate(AccelerationState.UP);
	}
	
	@Override
	public void down() {
		accelerate(AccelerationState.DOWN);
	}

}
