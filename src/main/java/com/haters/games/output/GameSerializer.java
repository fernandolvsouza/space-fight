package com.haters.games.output;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.haters.games.physics.Bullet;
import com.haters.games.physics.SpaceShip;
import com.haters.games.physics.SpaceWorld;

public class GameSerializer {

	class TO{
		List<SpaceShipTO> bots = new ArrayList<GameSerializer.SpaceShipTO>();
		SpaceShipTO player;	
	}
	
	class SpaceShipTO{
		
		float x,y;
		
		public SpaceShipTO(SpaceShip ship) {
			x = ship.getBody().getPosition().x;
			y = ship.getBody().getPosition().y;
		}
	
	}
	
	static Gson g = new GsonBuilder().create() ;
	public void serialize(SpaceWorld spaceWorld, List<SpaceShip> bots,
			List<SpaceShip> players, Writer out) throws IOException {
		JsonWriter jw = new JsonWriter(out) ;
		jw.beginObject()
				.name("bots").beginArray();

		for (SpaceShip bot : bots) {
			shipToJson(bot,jw);
		}
		jw.endArray()
				.name("players").beginArray();
		for (SpaceShip p : players) {
			shipToJson(p,jw);
		}
		jw.endArray().endObject().flush();
		out.append("\n");
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{
		jw.beginObject().
			name("id").value(ship.getId()).
			name("angle").value(String.format("%.2f", ship.getBody().getAngle())).
			name("x").value(String.format("%.2f", ship.getBody().getPosition().x)).
			name("y").value(String.format("%.2f", ship.getBody().getPosition().y));
			
			/*jw.name("fixes");
			jw.beginArray();
			
			Fixture f =  ship.getBody().getFixtureList();
			while (f != null) {
				jw.beginArray();
				for (Vec2 v : ((PolygonShape)f.getShape()).getVertices()){
					jw.value(v.x).value(v.y);
				}
				jw.endArray();
				f = f.getNext() ;
			}
			jw.endArray();
			*/
			jw.name("bullets");
			jw.beginArray();
			for (Bullet b : ship.getBullets()) {
				jw.beginObject().
				//name("id").value(b.hashCode()).
				name("x").value(String.format("%.2f", b.getBody().getPosition().x)).
				name("y").value(String.format("%.2f", b.getBody().getPosition().y)).
				endObject();
			}
			jw.endArray();	
		return jw.endObject();
	}
	
	

}
