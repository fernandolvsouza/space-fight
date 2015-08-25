package com.haters.games.output;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.haters.games.physics.Bullet;
import com.haters.games.physics.Garbage;
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
					serialize(player.getShipsInRange(),player.getBulletsInRange(),player.getGarbagesInRange(),bots.size(),players.size(),jw);
			jw.endObject();
			out.append("\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonWriter serialize(Set<SpaceShip> ships,Set<Bullet> bullets, Set<Garbage> garbages, int totalbots, int totalplayers, JsonWriter jw) throws IOException {
		jw.beginObject().
			name("totalbots").value(totalbots).
			name("totalplayers").value(totalplayers).
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
			
			name("trash").
			beginArray();
			for (Garbage g : garbages) {
				garbageToJson(g,jw);
			}
			jw.endArray().
		endObject();
		return jw;
	}
	
	private JsonWriter garbageToJson(Garbage g, JsonWriter jw) throws IOException{
		jw.beginObject().
		name("id").value(g.getId()).
		name("x").value(String.format("%.2f", g.getBody().getPosition().x)).
		name("y").value(String.format("%.2f", g.getBody().getPosition().y)).
		name("type").value("").
		endObject();
		return jw;
	}

	private JsonWriter bulletToJson(Bullet b, JsonWriter jw) throws IOException{
		jw.beginObject().
		name("id").value(b.getId()).
		name("x").value(new BigDecimal(b.getBody().getPosition().x).setScale(2,RoundingMode.HALF_DOWN)).
		name("y").value(new BigDecimal(b.getBody().getPosition().y).setScale(2,RoundingMode.HALF_DOWN)).
		name("type").value("bullet").
		name("angle").value(new BigDecimal(b.getVelocityAngle()).setScale(2,RoundingMode.HALF_DOWN)).
		endObject();
		return jw;
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{
		jw.beginObject().
			name("id").value(ship.getId()).
			name("type").value(ship.getType()).
			name("angle").value(new BigDecimal(ship.getAngle())).
			name("x").value(positionToJson(ship.getBody().getPosition().x)).
			name("y").value(positionToJson(ship.getBody().getPosition().y)).
			name("energy").value(new BigDecimal(ship.getCurrentEnergy()*100/ship.getTotalEnergy()).setScale(0,RoundingMode.DOWN)).
			name("isbot").value(booleanToJson(ship.isbot())).
			name("isdamaged").value(booleanToJson(ship.isDamaged()));
		
		return jw.endObject();
	}
	
	private int booleanToJson(boolean b){
		return b ? 1 : 0;
	}
	private BigDecimal positionToJson(float p){
		return new BigDecimal(p).setScale(2,RoundingMode.HALF_DOWN);
	}

}
