package com.haters.games.input;


import java.util.List;

import com.haters.games.physics.SpaceShip;

public interface GameInputStream {

    float[] getMouseMoveEvent(SpaceShip player);

    Object[] getBeBornEvent(SpaceShip player);

    boolean hasLeftEvent(SpaceShip player);

    boolean hasRightEvent(SpaceShip player);

    boolean hasUpEvent(SpaceShip player);

    boolean hasDownEvent(SpaceShip player);

    boolean hasFireEvent(SpaceShip player);

    boolean hasRemovePlayerEvent(SpaceShip player);

    boolean hasTryCaptureStarEvent(SpaceShip player);

    boolean hasNewPlayerEvent();

    List<Integer> getNewPlayers();

    void eraseNewPlayersEvents();

    void eraseRemovePlayersEvents();

    void eraseBeBornPlayersEvents();

    void processEvents();


}
