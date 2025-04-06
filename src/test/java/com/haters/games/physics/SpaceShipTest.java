package com.haters.games.physics;

import com.haters.games.BaseTest;
import com.haters.games.Group;
import org.jbox2d.common.Vec2;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SpaceShipTest extends BaseTest {
    private SpaceShip ship;
    private Group group;

    @Before
    public void setUp() {
        super.setUp();
        group = new Group(1, GroupColor.BLUE);
        ship = new CircleSpaceShip(world, createVec2(0, 0), group);
    }

    @Test
    public void shouldCreateSpaceShipWithCorrectInitialState() {
        assertThat(ship.getGroup()).isEqualTo(group);
        assertThat(ship.getLifePoints()).isEqualTo(100);
        assertThat(ship.getBody().getPosition()).isEqualTo(new Vec2(0, 0));
        assertThat(ship.getBody().getLinearVelocity()).isEqualTo(new Vec2(0, 0));
    }

    @Test
    public void shouldAccelerateWhenThrustIsApplied() {
        // Given
        float initialSpeed = ship.getBody().getLinearVelocity().length();
        
        // When
        ship.setAccelerationState(AccelerationState.FORWARD);
        stepWorld();
        
        // Then
        float finalSpeed = ship.getBody().getLinearVelocity().length();
        assertThat(finalSpeed).isGreaterThan(initialSpeed);
    }

    @Test
    public void shouldRotateWhenTurnStateIsChanged() {
        // Given
        float initialAngle = ship.getBody().getAngle();
        
        // When
        ship.setTurnState(TurnState.RIGHT);
        stepWorld();
        
        // Then
        float finalAngle = ship.getBody().getAngle();
        assertThat(finalAngle).isNotEqualTo(initialAngle);
    }

    @Test
    public void shouldTakeDamageWhenHit() {
        // Given
        int initialLife = ship.getLifePoints();
        
        // When
        ship.hit(25);
        
        // Then
        assertThat(ship.getLifePoints()).isEqualTo(initialLife - 25);
    }

    @Test
    public void shouldDieWhenLifePointsReachZero() {
        // When
        ship.hit(100);
        
        // Then
        assertThat(ship.isDestroyed()).isTrue();
    }
} 