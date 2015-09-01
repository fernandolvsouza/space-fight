package com.haters.games.physics;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.haters.games.output.SERIALIZER_TYPE;

import java.util.Random;

public class Energy implements GameEntity, GameSerializable{
	
	private int id;
	private SpaceWorld world;
	private Body body;
	
	public Energy(SpaceWorld world) {
		this.world = world;
		this.id = Sequence.getSequence();
		init();
	}
	
	private void init(){
		 // body definition
        BodyDef bd = new BodyDef();
        bd.setType(BodyType.DYNAMIC);
        bd.linearDamping = 1.0f;
        bd.angularDamping = 5.0f;

        // shape definition
        CircleShape shape = new CircleShape();
		int min_r = 1;
		int max_r = 10;
		Random r = new Random();
		int radius = r.nextInt(max_r-min_r + 1) + min_r;

        shape.setRadius(radius);
        shape.m_p.set(0,0);

        // fixture definition
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.05f;


        // create dynamic body
        bd.setPosition(world.getRandomPosition());
        this.body = this.world.getWorld().createBody(bd);
        this.body.createFixture(fd);

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
		Energy other = (Energy) obj;
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

	@Override
	public void setReadyToDestroy(boolean b) {

	}

	@Override
	public boolean readyToDestroy() {
		return false;
	}

	@Override
	public void destroy() {

	}
	
	public SERIALIZER_TYPE getType(){
		return SERIALIZER_TYPE.ENERGY;
	}
}
