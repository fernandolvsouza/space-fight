package com.haters.games.physics;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by flvs on 8/9/15.
 */
public abstract class BasicShip {

    protected int id;

    protected Body body;
    protected World world;
    protected List<Destroyable> killthen;

    private LinkedList<Bullet> bullets = new LinkedList<Bullet>();
    private long lastFireTime = 0;
    private int bulletsSequence = 0;
    private final static int maximumActiveBullets = 30;

    protected boolean isbot = true;
    private int currentEnergy;
    protected final long timeCreated = new Date().getTime();

    protected DetectEntitiesCallback detectionCallback;

    protected final static float attackModeLinearDamping = 1.0f;
    protected final static float cruiseModeLinearDamping = 3.0f;

    protected BasicShip(World world, Vec2 pos, int id, List<Destroyable> killthen) {
        this.id = id;
        this.world = world;
        this.killthen = killthen;
        this.currentEnergy = this.getTotalEnergy();
        init(pos);
    }

    protected BasicShip(World world, int id, List<Destroyable> killthen, boolean isbot) {
        this.id = id;
        this.world = world;
        this.killthen = killthen;
        this.isbot = isbot;
        this.currentEnergy = this.getTotalEnergy();
        init(new Vec2(0,10));

    }

    public int getId() {
        return id;
    }

    public Body getBody() {
        return body;
    }

    public World getWorld(){
        return world;
    }

    public List<Bullet> getBullets() {
        return this.bullets;
    }


    public void fireToPosition(Vec2 to) {

        long now = new Date().getTime();
        if( now - lastFireTime > Bullet.FireFrequency) {
            Bullet bullet = Bullet.create((SpaceShip)this,bulletsSequence++);
            this.bullets.add(bullet);
            if(to == null)
                bullet.fire();
            else
                bullet.fireToPosition(to);
            lastFireTime = now;
        }
        removeExcessBullets();
    }

    protected void removeExcessBullets(){
        int wastebullets = bullets.size() - maximumActiveBullets;
        if(wastebullets > 0){
            for (int i = 0; i < wastebullets; i++) {
                Bullet b = bullets.pop();
                killthen.add(b);
            }
        }
    }

    protected float angleBetweenToVector(Vec2 v1, Vec2 v2){
        float angle = MathUtils.atan2(v2.y, v2.x) - MathUtils.atan2(v1.y, v1.x);
        if (angle < 0) angle += 2 * MathUtils.PI;
        return angle;

    }

    @Override
    public String toString() {
        return "plane : {id: "+id+"}";
    }

    public int getCurrentEnergy(){
        return this.currentEnergy;
    }

    public void damage(Bullet b) {
        this.currentEnergy -= b.getDamage();
    }

    public void destroy() {
        for (Bullet bullet : this.bullets) {
            bullet.destroyCascade();
            bullet = null;
        }
        bullets.clear();
        this.world.destroyBody(this.body);
    }

    public Set<SpaceShip> getShipsInRange() {
        return this.detectionCallback.planes;
    }

    public Boundaries getBoundsInRange() {
        return this.detectionCallback.boundaries;
    }

    public void detectGameEntities() {
        detectionCallback.reset();
        AABB aabb = new AABB();
        aabb.lowerBound.set(new Vec2(this.body.getPosition().x - getEnemyDetectRange(),this.body.getPosition().y - getEnemyDetectRange()));
        aabb.upperBound.set(new Vec2(this.body.getPosition().x + getEnemyDetectRange(),this.body.getPosition().y + getEnemyDetectRange()));
        this.world.queryAABB(detectionCallback, aabb);
    }


    public boolean isbot(){
        return isbot;
    }

    public BasicShip setAttackMode() {
        this.body.m_linearDamping = attackModeLinearDamping;
        return this;
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
        BasicShip other = (BasicShip) obj;
        if (id != other.id)
            return false;
        if (isbot != other.isbot)
            return false;
        if (timeCreated != other.timeCreated)
            return false;
        return true;
    }


    protected abstract void init(Vec2 vec2);
    protected abstract int getTotalEnergy();
    protected abstract float getEnemyDetectRange();

}
