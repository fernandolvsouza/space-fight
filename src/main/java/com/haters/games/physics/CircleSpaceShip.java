package com.haters.games.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.List;
import java.util.Set;

/**
 * Created by flvs on 8/10/15.
 */
public class CircleSpaceShip extends BasicShip implements SpaceShip,Destroyable, GameEntity{

    static public CircleSpaceShip create(World world,int id, List<Destroyable> killthen, boolean isbot){
        return new CircleSpaceShip(world,id,killthen,isbot);
    }
    static public CircleSpaceShip create(World world, Vec2 pos, int id, List<Destroyable> killthen){
        return new CircleSpaceShip(world,pos,id,killthen);
    }

    protected CircleSpaceShip(World world, Vec2 pos, int id, List<Destroyable> killthen) {
        super(world, pos, id, killthen);
    }

    protected CircleSpaceShip(World world, int id, List<Destroyable> killthen, boolean isbot) {
        super(world, id, killthen, isbot);
    }

    @Override
    protected void init(Vec2 pos) {

        // body definition
        BodyDef bd = new BodyDef();
        bd.setType(BodyType.DYNAMIC);
        bd.linearDamping = 0.7f;
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
        this.body = this.world.createBody(bd);
        this.body.createFixture(fd);

        //this.body.setTransform(this.body.getPosition(),90 * MathUtils.DEG2RAD);
        this.body.setUserData(this);
        this.detectionCallback = new DetectEntitiesCallback(this);

    }

    public float getRadius(){
        return 2.0f;
    }

    @Override
    protected int getTotalEnergy() {
        return isbot ? 10 : 100;
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
    public String getType() {
        return "circle";
    }
    
	@Override
	public float getAngle() {
		
		return body.getAngle();
		
		/*Vec2 center = body.getWorldPoint(new Vec2(0, 0));
		Vec2 direction = mouse_pos.sub(center);
		return MathUtils.atan2(direction.y,direction.x);*/
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
