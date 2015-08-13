package com.haters.games.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.haters.games.physics.Bullet;
import com.haters.games.physics.PolygonSpaceShip;
import com.haters.games.physics.SpaceShip;

public class GameSerializer {
	
	static Gson g = new GsonBuilder().create() ;

	public void serializeForPlayer(SpaceShip player, Writer out) {
		try {
			JsonWriter jw = new JsonWriter(out) ;
			jw.beginObject().
					name("player");
					shipToJson(player,jw);
			jw.name("scene");
					serialize(player.getShipsInRange(),jw);
			jw.endObject();
			out.append("\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonWriter serialize(Set<SpaceShip> ships,JsonWriter jw) throws IOException {
		jw.beginObject().name("ships").beginArray();
		for (SpaceShip p : ships) {
			shipToJson(p,jw);
		}
		jw.endArray().endObject();
		return jw;
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{
		jw.beginObject().
			name("id").value(ship.getId()).
			name("type").value(ship.getType()).
			name("angle").value(String.format("%.2f", ship.getBody().getAngle())).
			name("x").value(String.format("%.2f", ship.getBody().getPosition().x)).
			name("y").value(String.format("%.2f", ship.getBody().getPosition().y)).
			name("bullets");
			jw.beginArray();
			for (Bullet b : ship.getBullets()) {
				jw.beginObject().
				name("id").value(String.format("%d", b.getId())).
				name("x").value(String.format("%.2f", b.getBody().getPosition().x)).
				name("y").value(String.format("%.2f", b.getBody().getPosition().y)).
				endObject();
			}
			jw.endArray();	
		return jw.endObject();
	}
}
