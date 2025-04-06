package com.haters.games.physics;

import com.haters.games.BaseTest;
import com.haters.games.Group;
import org.jbox2d.common.Vec2;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BulletTest extends BaseTest {
    
    private Group group;
    private SpaceShip ship;
    private BulletFactory bulletFactory;

    @Before
    public void setUp() {
        super.setUp();
        group = new Group(1, GroupColor.BLUE);
        ship = new CircleSpaceShip(world, createVec2(0, 0), group);
        bulletFactory = new BulletFactory(world);
    }

    @Test
    public void shouldCreateSimpleBullet() {
        // Given
        Vec2 position = createVec2(1, 1);
        Vec2 velocity = createVec2(10, 0);
        
        // When
        Bullet bullet = bulletFactory.createBullet(BulletType.SIMPLE, position, velocity, group);
        
        // Then
        assertThat(bullet).isInstanceOf(SimpleBullet.class);
        assertThat(bullet.getBody().getPosition()).isEqualTo(position);
        assertThat(bullet.getGroup()).isEqualTo(group);
    }

    @Test
    public void shouldCreateChaseBullet() {
        // Given
        Vec2 position = createVec2(1, 1);
        Vec2 velocity = createVec2(10, 0);
        
        // When
        Bullet bullet = bulletFactory.createBullet(BulletType.CHASE, position, velocity, group);
        
        // Then
        assertThat(bullet).isInstanceOf(ChaseBullet.class);
        assertThat(bullet.getBody().getPosition()).isEqualTo(position);
        assertThat(bullet.getGroup()).isEqualTo(group);
    }

    @Test
    public void shouldCreateStarCaptureBullet() {
        // Given
        Vec2 position = createVec2(1, 1);
        Vec2 velocity = createVec2(10, 0);
        
        // When
        Bullet bullet = bulletFactory.createBullet(BulletType.STAR_CAPTURE, position, velocity, group);
        
        // Then
        assertThat(bullet).isInstanceOf(StarCaptureBullet.class);
        assertThat(bullet.getBody().getPosition()).isEqualTo(position);
        assertThat(bullet.getGroup()).isEqualTo(group);
    }

    @Test
    public void shouldDamageShipOnCollision() {
        // Given
        Vec2 position = createVec2(0, 0);
        Vec2 velocity = createVec2(10, 0);
        Bullet bullet = bulletFactory.createBullet(BulletType.SIMPLE, position, velocity, new Group(2, GroupColor.RED));
        int initialLife = ship.getLifePoints();
        
        // When
        stepWorld();
        
        // Then
        assertThat(ship.getLifePoints()).isLessThan(initialLife);
        assertThat(bullet.isDestroyed()).isTrue();
    }

    @Test
    public void shouldNotDamageShipFromSameGroup() {
        // Given
        Vec2 position = createVec2(0, 0);
        Vec2 velocity = createVec2(10, 0);
        Bullet bullet = bulletFactory.createBullet(BulletType.SIMPLE, position, velocity, group);
        int initialLife = ship.getLifePoints();
        
        // When
        stepWorld();
        
        // Then
        assertThat(ship.getLifePoints()).isEqualTo(initialLife);
        assertThat(bullet.isDestroyed()).isFalse();
    }
} 