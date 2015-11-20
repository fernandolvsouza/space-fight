package com.haters.games.physics;

/**
 * Created by flvs on 11/8/15.
 */
public enum WeaponType {
    FAST_BULLET,STAR_CAPTURE_BULLET,CHASE_BULLET, PARALYSIS;

    public long fireFrequency(){
        if(this == FAST_BULLET) {
            return 300;
        }else if(this == STAR_CAPTURE_BULLET) {
            return 10000;
        }

        return 100000000;
    }
}
