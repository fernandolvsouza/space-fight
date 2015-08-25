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
	
	private static Gson g = new GsonBuilder().create() ;

	public void serializeForPlayer(SpaceShip player, List<SpaceShip> bots, List<SpaceShip> players, Writer out) {
		try {
			JsonWriter jw = new JsonWriter(out) ;
			serialize(player, player.getShipsInRange(),player.getBulletsInRange(),player.getGarbagesInRange(),bots.size(),players.size(),jw);

			out.append("\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonWriter serialize(SpaceShip player,Set<SpaceShip> ships,Set<Bullet> bullets, Set<Garbage> garbages, int totalbots, int totalplayers, JsonWriter jw) throws IOException {
		jw.beginArray();
			
			shipToJson(player,jw);
			
			jw.value(totalbots).
			value(totalplayers).
			beginArray();
			
			for (SpaceShip p : ships) {
				shipToJson(p,jw);
			}
			jw.endArray().			
			
			beginArray();
			for (Bullet b : bullets) {
				bulletToJson(b,jw);
			}
			jw.endArray().
			
			beginArray();
			for (Garbage g : garbages) {
				garbageToJson(g,jw);
			}
			jw.endArray().
		endArray();
		return jw;
	}
	
	private JsonWriter garbageToJson(Garbage g, JsonWriter jw) throws IOException{
		jw.beginArray().
		value(g.getId()).
		value(2).
		value(tobigdecimal(g.getBody().getPosition().x)).
		value(tobigdecimal(g.getBody().getPosition().y)).
		endArray();
		return jw;
	}

	private JsonWriter bulletToJson(Bullet b, JsonWriter jw) throws IOException{
		jw.beginArray().
		value(b.getId()).
		value(1).
		value(tobigdecimal(b.getBody().getPosition().x)).
		value(tobigdecimal(b.getBody().getPosition().y)).
		value(tobigdecimal(b.getAngle())).
		endArray();
		return jw;
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{
		jw.beginArray().
			value(ship.getId()).
			value(0).
			value(tobigdecimal(ship.getBody().getPosition().x)).
			value(tobigdecimal(ship.getBody().getPosition().y)).
			value(tobigdecimal(ship.getAngle())).
			value(tobigdecimal(ship.getCurrentEnergy()*100/ship.getTotalEnergy(),0,RoundingMode.DOWN)).
			value(booleanToJson(ship.isbot())).
			value(booleanToJson(ship.isDamaged()));
		
		return jw.endArray();
	}
	
	private int booleanToJson(boolean b){
		return b ? 1 : 0;
	}
	
	private BigDecimal tobigdecimal(float p,int scale,RoundingMode mode){
		return new BigDecimal(p).setScale(scale,mode);
	}
	
	private BigDecimal tobigdecimal(float p){
		return new BigDecimal(p).setScale(2,RoundingMode.HALF_DOWN);
	}

}
