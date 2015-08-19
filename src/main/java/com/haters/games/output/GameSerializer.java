package com.haters.games.output;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.haters.games.physics.Bullet;
import com.haters.games.physics.PolygonSpaceShip;
import com.haters.games.physics.SpaceShip;

public class GameSerializer {
	
	static Gson g = new GsonBuilder().create() ;

	public void serializeForPlayer(SpaceShip player, List<SpaceShip> bots, List<SpaceShip> players, Writer out) {
		try {
			JsonWriter jw = new JsonWriter(out) ;
			jw.beginObject().
					name("player");
					shipToJson(player,jw);
			jw.name("scene");
					serialize(player.getShipsInRange(),player.getBulletsInRange(),bots.size(),players.size(),jw);
			jw.endObject();
			out.append("\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonWriter serialize(Set<SpaceShip> ships,Set<Bullet> bullets, int totalbots, int totalplayers, JsonWriter jw) throws IOException {
		jw.beginObject().
			name("totalbots").value(String.format("%d", totalbots)).
			name("totalplayers").value(String.format("%d", totalplayers)).
			name("ships").beginArray();
			
			for (SpaceShip p : ships) {
				shipToJson(p,jw);
			}
			jw.endArray().			
			
			name("bullets").
			beginArray();
			for (Bullet b : bullets) {
				bulletToJson(b,jw);
			}
			jw.endArray().
		endObject();
		return jw;
	}
	
	private JsonWriter bulletToJson(Bullet b, JsonWriter jw) throws IOException{
		jw.beginObject().
		name("id").value(String.format("%d", b.getId())).
		name("x").value(String.format("%.2f", b.getBody().getPosition().x)).
		name("y").value(String.format("%.2f", b.getBody().getPosition().y)).
		name("type").value("bullet").
		name("angle").value(String.format("%.2f", b.getVelocityAngle())).
		endObject();
		return jw;
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{
		jw.beginObject().
			name("id").value(ship.getId()).
			name("type").value(ship.getType()).
			name("angle").value(String.format("%.2f", ship.getAngle())).
			name("x").value(String.format("%.2f", ship.getBody().getPosition().x)).
			name("y").value(String.format("%.2f", ship.getBody().getPosition().y)).
			name("energy").value(String.format("%d", ship.getCurrentEnergy()*100/ship.getTotalEnergy())).
			name("isbot").value(ship.isbot());
		
		return jw.endObject();
	}

}
