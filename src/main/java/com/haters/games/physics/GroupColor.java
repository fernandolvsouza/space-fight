package com.haters.games.physics;

import java.awt.*;

/**
 * Created by flvs on 12/15/15.
 */
public enum GroupColor {
    YELLOW(Color.YELLOW),RED(Color.RED),GREEN(Color.GREEN),BLUE(Color.BLUE),CYAN(Color.CYAN),WHITE(Color.WHITE),PINK(Color.PINK),ORANGE(Color.ORANGE);

    private Color color;

    GroupColor(Color color) {
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }

}
