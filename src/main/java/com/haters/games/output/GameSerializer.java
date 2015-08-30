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
import com.haters.games.physics.Energy;
import com.haters.games.physics.SpaceShip;

public class GameSerializer {
	
	
	private static Gson g = new GsonBuilder().create() ;

	public void serializeForPlayer(SpaceShip player, List<SpaceShip> bots, List<SpaceShip> players, Writer out) {
		try {
			JsonWriter jw = new JsonWriter(out) ;
			serialize(player, player.getShipsInRange(),player.getBulletsInRange(),player.getGarbagesInRange(),bots.size(),players.size(),jw);
			out.write('\n');
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonWriter serialize(SpaceShip player,Set<SpaceShip> ships,Set<Bullet> bullets, Set<Energy> energies, int totalbots, int totalplayers, JsonWriter jw) throws IOException {
		jw.beginArray();
			
			jw.
			value(totalbots).
			value(totalplayers);

		
			shipToJson(player,jw);
			
			
			for (SpaceShip p : ships) {
				shipToJson(p,jw); // 9 multiple alements
			}
				

			for (Bullet b : bullets) {
				bulletToJson(b,jw); // 5 multiple alements
			}
			

			for (Energy g : energies) {
				garbageToJson(g,jw); // 4 multiple alements
			}

		jw.endArray();
		return jw;
	}
	
	private JsonWriter garbageToJson(Energy g, JsonWriter jw) throws IOException{ //4 attributes
		jw.
		value(g.getType().ordinal()).
		value(g.getId()).
		value(tobigdecimal(g.getBody().getPosition().x)).
		value(tobigdecimal(g.getBody().getPosition().y));

		
		return jw;
	}

	private JsonWriter bulletToJson(Bullet b, JsonWriter jw) throws IOException{ //5 attributes
		jw.
		value(b.getType().ordinal()).
		value(b.getId()).
		value(tobigdecimal(b.getBody().getPosition().x)).
		value(tobigdecimal(b.getBody().getPosition().y)).
		value(tobigdecimal(b.getAngle()));

		
		return jw;
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{ //8 attributes
		jw.
		value(ship.getType().ordinal()).
		value(ship.getId()).
		value(tobigdecimal(ship.getBody().getPosition().x)).
		value(tobigdecimal(ship.getBody().getPosition().y)).
		value(tobigdecimal(ship.getAngle())).
		value(tobigdecimal(ship.getCurrentEnergy()*100/ship.getTotalEnergy(),0,RoundingMode.DOWN)).
		value(booleanToJson(ship.isbot())).
		value(booleanToJson(ship.isDamaged())).
		value(ship.getName());
		
		return jw;
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
