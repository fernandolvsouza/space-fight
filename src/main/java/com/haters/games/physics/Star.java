package com.haters.games.physics;

import com.haters.games.Group;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.haters.games.output.SERIALIZER_TYPE;

import java.util.Random;

public class Star implements GameEntity, GameSerializable{
	
	private int id;
	private SpaceWorld world;
	private Body body;
	private int radius;
	private int range;
	private Group group;


	public Star(SpaceWorld world) {
		this.world = world;
		this.id = Sequence.getSequence();
		init();
	}
	
	private void init(){
		 // body definition
        BodyDef bd = new BodyDef();
        bd.setType(BodyType.STATIC);

        // shape definition

		int min_r = 5;
		int max_r = 10;
		Random r = new Random();
		this.radius = r.nextInt(max_r-min_r + 1) + min_r;

		//Star
		CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        shape.m_p.set(0,0);

        // fixture definition
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.05f;


		//range sensor
		range = radius * 7;

		CircleShape sensorShape = new CircleShape();
		sensorShape.setRadius(range);
		sensorShape.m_p.set(0,0);

		// fixture definition
		FixtureDef rangeSensorFix = new FixtureDef();
		rangeSensorFix.shape = sensorShape;
		rangeSensorFix.setSensor(true);

        // create dynamic body
        bd.setPosition(world.getRandomPosition(30));
        this.body = this.world.getWorld().createBody(bd);
        this.body.createFixture(fd);
		this.body.createFixture(rangeSensorFix);

        this.body.setUserData(this);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Star other = (Star) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public Body getBody() {
		return body;
	}

	
	public SERIALIZER_TYPE getType(){
		return SERIALIZER_TYPE.ENERGY;
	}

	public int getRadius() {
		return radius;
	}

	public int getRange() {
		return range;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}
