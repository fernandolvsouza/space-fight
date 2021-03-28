package com.haters.games.physics;

public interface Destroyable {
    void setReadyToDestroy(boolean b);

    boolean readyToDestroy();

    void destroy();

}
