package com.haters.games.physics;

import com.haters.games.output.SERIALIZER_TYPE;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.Date;

/**
 * Created by flvs on 9/27/15.
 */
public class Base implements LifePointsEntity, Destroyable, GameEntity, GameSerializable {

    private final int id;
    private final SpaceWorld world;
    private Body body;
    private boolean readyToDestroy;
    private final int totalLife = 100;
    private int currentLife;
    private int energyLife;
    private long lastDamageTime = -1;
    private static final long damagePeriod = 300;

    public Base(SpaceWorld world) {
        this.world = world;
        this.id = Sequence.getSequence();
        this.currentLife = totalLife;
        init();
    }

    private void init() {
        // body definition
        BodyDef bd = new BodyDef();
        bd.setType(BodyType.DYNAMIC);


        // shape definition
        CircleShape shape = new CircleShape();
        int radius = 10;

        shape.setRadius(radius);
        shape.m_p.set(0, 0);

        // fixture definition
        FixtureDef fd = new FixtureDef();

        fd.shape = shape;
        fd.density = 0.5f;


        // create dynamic body
        bd.setPosition(world.getRandomPosition(30));
        this.body = this.world.getWorld().createBody(bd);
        this.body.createFixture(fd);

        this.body.setUserData(this);
        this.body.getFixtureList().setSensor(false);
        this.body.m_linearDamping = 3.0f;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        Base other = (Base) obj;
        return id == other.id;
    }

    public int getId() {
        return id;
    }

    public Body getBody() {
        return body;
    }

    public void setReadyToDestroy(boolean b) {
        readyToDestroy = b;
    }

    public boolean readyToDestroy() {
        return readyToDestroy;
    }

    @Override
    public void destroy() {
        this.world.getWorld().destroyBody(this.body);
    }

    @Override
    public int getCurrentLife() {
        return currentLife;
    }

    @Override
    public int getTotalLife() {
        return totalLife;
    }

    @Override
    public boolean isDamaged() {
        if (lastDamageTime < 0)
            return false;
        return (new Date().getTime() - lastDamageTime < damagePeriod);
    }

    @Override
    public void damage(SimpleBullet b) {
        this.currentLife -= b.getDamage();
        this.lastDamageTime = new Date().getTime();
    }

    public SERIALIZER_TYPE getType() {
        return SERIALIZER_TYPE.BASE;
    }
}
