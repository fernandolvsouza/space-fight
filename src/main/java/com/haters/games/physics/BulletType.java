package com.haters.games.physics;

/**
 * Created by flvs on 11/8/15.
 */
public enum BulletType {
    SIMPLE_BULLET,STAR_CAPTURE_BULLET,CHASE_BULLET;

    public long fireFrequency(){
        if(this == SIMPLE_BULLET) {
            return 300;
        }if(this == CHASE_BULLET) {
            return 500;
        }else if(this == STAR_CAPTURE_BULLET) {
            return 10000;
        }

        return 100;
    }
}
