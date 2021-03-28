package com.haters.games.physics;

import com.haters.games.output.SERIALIZER_TYPE;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


import java.util.Date;
import java.util.Iterator;

/**
 * Created by flvs on 12/8/15.
 */
public class ChaseBullet extends SimpleBullet implements Bullet, EntityWithDetector, TimeLimitedPowers {

    private final DetectEntitiesCallback detectionCallback;
    private final EntityDetector detector;
    private final long detectionFrequency = 500;
    private long lastdetection = 0;


    public ChaseBullet(SpaceShip ship) {
        super(ship);
        this.detectionCallback = new DetectEntitiesCallback(this);
        this.detector = new EntityDetector(this, detectionCallback);
    }

    @Override
    public double getDamage() {
        return super.getDamage() * 2;
    }

    @Override
    public SERIALIZER_TYPE getType() {
        return SERIALIZER_TYPE.CHASE_BULLET;
    }

    @Override
    public DetectEntitiesCallback getDetectionCallback() {
        return this.detectionCallback;
    }

    @Override
    public World getWorld() {
        return plane.getWorld();
    }

    @Override
    public float getEnemyDetectRange() {
        return 25;
    }

    @Override
    public void detectGameEntities() {
        if (new Date().getTime() - lastdetection < detectionFrequency)
            return;

        this.detector.detectGameEntities();
        lastdetection = new Date().getTime();
    }

    public void chase() {

        SpaceShip target = null;
        Iterator<SpaceShip> it = detectionCallback.planes.iterator();

        while (target == null && it.hasNext()) {
            SpaceShip ship = it.next();
            if (ship.getGroup().equals(this.getShip().getGroup()))
                continue;
            target = ship;

        }

        if (target == null)
            return;

        Vec2 chasePos = new Vec2(target.getBody().getPosition());

        Vec2 bodyPos = getBody().getPosition();

        float strength = 50f;

        Vec2 sub = chasePos.sub(bodyPos);
        sub.normalize();
        Vec2 mul = sub.mul(strength);
        getBody().applyForce(mul, bodyPos);

    }
}
