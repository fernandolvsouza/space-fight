package com.haters.games.physics;

import com.haters.games.output.SERIALIZER_TYPE;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Created by flvs on 11/2/15.
 */
public class StarCaptureBullet extends SimpleBullet implements Bullet {

    public StarCaptureBullet(SpaceShip ship) {
        super(ship);
    }

    @Override
    public SERIALIZER_TYPE getType() {
        return SERIALIZER_TYPE.START_CAPTURE_BULLET;
    }
}
