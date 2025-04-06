package com.haters.games.physics;

import com.haters.games.BaseTest;
import com.haters.games.Group;
import com.haters.games.input.GameInputStream;
import com.haters.games.output.NetworkOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GameLogicTest extends BaseTest {
    
    @Mock
    private GameInputStream inputStream;
    
    @Mock
    private NetworkOutputStream outputStream;
    
    private GameLogic gameLogic;
    private SpaceWorld spaceWorld;

    @Before
    public void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        spaceWorld = new SpaceWorld(null, world);
        gameLogic = new GameLogic(spaceWorld, inputStream, outputStream);
    }

    @Test
    public void shouldInitializeGameWithCorrectState() {
        // When
        gameLogic.init();
        
        // Then
        assertThat(spaceWorld.getWorld()).isNotNull();
        verify(outputStream).init();
    }

    @Test
    public void shouldStepGameLogicCorrectly() {
        // Given
        gameLogic.init();
        float timeStep = 1.0f/60.0f;
        
        // When
        gameLogic.step(timeStep, 8, 3);
        
        // Then
        verify(outputStream).serialize(any());
    }

    @Test
    public void shouldCreateShipForNewGroup() {
        // Given
        gameLogic.init();
        Group group = new Group(1, GroupColor.BLUE);
        
        // When
        SpaceShip ship = gameLogic.createShip(group);
        
        // Then
        assertThat(ship).isNotNull();
        assertThat(ship.getGroup()).isEqualTo(group);
    }

    @Test
    public void shouldHandleCollisionsBetweenEntities() {
        // Given
        gameLogic.init();
        Group group1 = new Group(1, GroupColor.BLUE);
        Group group2 = new Group(2, GroupColor.RED);
        SpaceShip ship1 = gameLogic.createShip(group1);
        SpaceShip ship2 = gameLogic.createShip(group2);
        
        // When
        ship1.getBody().setTransform(createVec2(0, 0), 0);
        ship2.getBody().setTransform(createVec2(0, 0), 0);
        stepWorld();
        
        // Then
        assertThat(ship1.getLifePoints()).isLessThan(100);
        assertThat(ship2.getLifePoints()).isLessThan(100);
    }
} 