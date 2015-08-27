package com.haters.games.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.haters.games.output.SERIALIZER_TYPE;

import java.util.List;
import java.util.Set;

/**
 * Created by flvs on 8/10/15.
 */
public class CircleSpaceShip extends BasicShip implements SpaceShip,Destroyable, GameEntity{

    static public CircleSpaceShip create(SpaceWorld world,int id, DestroyPool killthen, boolean isbot){
        return new CircleSpaceShip(world,id,killthen,isbot);
    }
    static public CircleSpaceShip create(SpaceWorld world, Vec2 pos, int id, DestroyPool killthen){
        return new CircleSpaceShip(world,pos,id,killthen);
    }

    protected CircleSpaceShip(SpaceWorld world, Vec2 pos, int id, DestroyPool killthen) {
        super(world, pos, id, killthen);
    }

    protected CircleSpaceShip(SpaceWorld world, int id, DestroyPool killthen, boolean isbot) {
        super(world, id, killthen, isbot);
    }

    @Override
    protected void init(Vec2 pos) {

        // body definition
        BodyDef bd = new BodyDef();
        bd.setType(BodyType.DYNAMIC);
        bd.linearDamping = 1.0f;
        bd.angularDamping = 5.0f;

        // shape definition
        CircleShape shape = new CircleShape();
        shape.setRadius(2.0f);
        shape.m_p.set(0,0);

        // fixture definition
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1;


        // create dynamic body
        bd.setPosition(pos);
        this.body = this.world.getWorld().createBody(bd);
        this.body.createFixture(fd);

        //this.body.setTransform(this.body.getPosition(),90 * MathUtils.DEG2RAD);
        this.body.setUserData(this);
        this.detectionCallback = new DetectEntitiesCallback(this);

    }

    public float getRadius(){
        return 2.0f;
    }

    @Override
    public int getTotalEnergy() {
        return isbot ? 1000000 : 100;
    }

    @Override
    protected float getEnemyDetectRange() {
    	return isbot ? 50 : 100;
    }

    private void goToDirection(Vec2 direction){
        direction.normalize();
        float impulse = 7.5f;
        this.body.applyLinearImpulse(new Vec2(direction.x * impulse, direction.y * impulse), this.body.getPosition(), true);
    }

    @Override
    public SERIALIZER_TYPE getType() {
        return SERIALIZER_TYPE.CIRCLE;
    }
    
	@Override
	public float getAngle() {
		return body.getAngle();
	}
	@Override
	public void setMousePosition(Vec2 mouse_pos) {
		this.mouse_pos = mouse_pos;
		rotateTo(mouse_pos);
	}
	@Override
	public Vec2 getMousePosition() {
		return mouse_pos;
	}
	
	@Override
	public void left() {
		goToDirection(body.getWorldVector(new Vec2(0, +1)));	
	}
	
	@Override
	public void right() {
		goToDirection(body.getWorldVector(new Vec2(0, -1)));
	}
	
	@Override
	public void up() {
		goToDirection(body.getWorldVector(new Vec2(1, 0)));
	}
	
	@Override
	public void down() {
		goToDirection(body.getWorldVector(new Vec2(-1, 0)));
	}
}
