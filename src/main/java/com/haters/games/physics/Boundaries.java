package com.haters.games.physics;

import com.haters.games.output.SERIALIZER_TYPE;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Boundaries implements Destroyable,GameEntity, GameSerializable{
	Body body;
	private Vec2[] vertices;

	public Body getBody() {
		return body;
	}

	private SpaceWorld spaceWorld;
	
	public Boundaries(SpaceWorld spaceWorld) {
		this.spaceWorld = spaceWorld;
	}

	public void setup() {
		int width = spaceWorld.getRadius();
		
		// body definition
		BodyDef bd = new BodyDef();
		bd.setType(BodyType.STATIC);
		bd.setPosition(new Vec2(0, 0));

		// shape definition
		EdgeShape shape = new EdgeShape();

		// fixture definition
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1;

		// body
		body = spaceWorld.getWorld().createBody(bd);

		Vec2 v1 = new Vec2(-width/2,0);
		Vec2 v2 =  new Vec2(width/2, 0);
		Vec2 v3 = new Vec2(width/2, width);
		Vec2 v4 = new Vec2(-width/2, width);

		shape.set(v1,v2);
		body.createFixture(fd);

		shape.set(v2,v3);
		body.createFixture(fd);

		shape.set(v3,v4);
		body.createFixture(fd);

		shape.set(v4,v1);
		body.createFixture(fd);
		
		body.setUserData(this);
		this.vertices = new Vec2[]{v1,v2,v3,v4};
	}

	@Override
	public void destroy() {
		this.spaceWorld.getWorld().destroyBody(this.body);
		
	}

	@Override
	public void setReadyToDestroy(boolean b) {
				
	}

	@Override
	public boolean readyToDestroy() {
		return false;
	}

	@Override
	public SERIALIZER_TYPE getType() {
		return SERIALIZER_TYPE.EDGE;
	}

	public Vec2[] getVertices(){
		return vertices;
	}
}
