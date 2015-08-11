package com.haters.games.physics;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Boundaries implements Destroyable,GameEntity{
	Body body;
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
		
		/*int[] angles = new int[20];
		for (int i=0;i<angles.length; i++) {
			angles[i] = i*360/angles.length;
		}*/
		
		/*for (int i=0;i<angles.length; i++) {
			Vec2 p1 = new Vec2(MathUtils.sin(angles[i]*MathUtils.DEG2RAD),MathUtils.cos(angles[i]*MathUtils.DEG2RAD)).mul(spaceWorld.getRadius());
			Vec2 p2 = new Vec2(MathUtils.sin(angles[(i+1 == angles.length ? 0 : i+1)]*MathUtils.DEG2RAD),
					MathUtils.cos(angles[(i+1 == angles.length ? 0 : i+1)]*MathUtils.DEG2RAD)).mul(spaceWorld.getRadius());
			shape.set(p1,p2);//(width / 2, 1, new Vec2(0, 0), 0);
			body.createFixture(fd);
		}*/

		// add four walls to the static body
		 
		/*shape.set(p1,p2);//(width / 2, 1, new Vec2(0, 0), 0);
		body.createFixture(f);*/
		 
		shape.set(new Vec2(-width/2,0),new Vec2(width/2, 0));//(width / 2, 1, new Vec2(0, 0), 0);
		body.createFixture(fd);

		//shape.setAsBox(1, width / 2, new Vec2(-width / 2, width / 2), 0);
		shape.set(new Vec2(width/2,0),new Vec2(width/2, width));
		body.createFixture(fd);

	//	shape.setAsBox(width / 2, 1, new Vec2(0, width), 0);
		shape.set(new Vec2(width/2,width),new Vec2(-width/2, width));
		body.createFixture(fd);

		//shape.setAsBox(1, width / 2, new Vec2(width / 2, width / 2), 0);
		shape.set(new Vec2(-width/2,width),new Vec2(-width/2, 0));
		body.createFixture(fd);
		
		body.setUserData(this);
	}

	@Override
	public void destroy() {
		this.spaceWorld.getWorld().destroyBody(this.body);
		
	}
}
