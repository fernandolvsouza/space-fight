package com.haters.games.physics;

import org.jbox2d.collision.shapes.CircleShape;
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
        bd.linearDamping = 0.5f;
        bd.angularDamping = 1000.0f;

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

    @Override
    protected int getTotalEnergy() {
        return 100;
    }

    @Override
    protected float getEnemyDetectRange() {
        return 70;
    }

    @Override
    public void turn(TurnState state) {
        Vec2 direction;
        if (state == TurnState.LEFT) {
            direction = new Vec2(-1, 0);
        } else{
            direction = new Vec2(1, 0);
        }
        goToDirection(direction);
    }

    @Override
    public void accelerate(AccelerationState state) {
        Vec2 direction;

        if (state == AccelerationState.UP) {
            direction = new Vec2(0, 1);
        }else {
            direction = new Vec2(0, -1);
        }
        goToDirection(direction);
    }

    private void goToDirection(Vec2 direction){
        direction.normalize();
        float impulse = 15;
        this.body.applyLinearImpulse(new Vec2(direction.x * impulse, direction.y * impulse), this.body.getPosition(), true);
    }

    @Override
    public boolean avoidColision() {
        return true;
    }

    @Override
    public void rotateTo(Vec2 position) {

    }

    @Override
    public boolean shouldFire(Set<SpaceShip> enemies) {
        return false;
    }

    @Override
    public String getType() {
        return "circle";
    }
}
