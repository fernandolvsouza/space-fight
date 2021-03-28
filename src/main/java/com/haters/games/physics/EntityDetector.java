package com.haters.games.physics;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;

public class EntityDetector {
    private final EntityWithDetector entity;
    protected DetectEntitiesCallback detectionCallback;

    public EntityDetector(EntityWithDetector entity, DetectEntitiesCallback detectionCallback) {
        this.detectionCallback = detectionCallback;
        this.entity = entity;
    }

    public void detectGameEntities() {
        entity.getDetectionCallback().reset();
        AABB aabb = new AABB();
        aabb.lowerBound.set(new Vec2(entity.getBody().getPosition().x - entity.getEnemyDetectRange(), entity.getBody().getPosition().y - entity.getEnemyDetectRange()));
        aabb.upperBound.set(new Vec2(entity.getBody().getPosition().x + entity.getEnemyDetectRange(), entity.getBody().getPosition().y + entity.getEnemyDetectRange()));
        entity.getWorld().queryAABB(entity.getDetectionCallback(), aabb);
    }

}
