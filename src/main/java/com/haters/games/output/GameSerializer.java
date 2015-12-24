package com.haters.games.output;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.haters.games.Group;
import com.haters.games.physics.*;

public class GameSerializer {

	private static Gson g = new GsonBuilder().create() ;

	public void serializeForPlayer(SpaceShip player, List<SpaceShip> bots, List<SpaceShip> players,List<Group> groups, List<Star> stars, boolean sendRank, int ranksize, Writer out) {
		try {
			
			JsonWriter jw = new JsonWriter(out) ;

			jw.beginArray().			
			value(bots.size()).
			value(players.size());
			
			shipToJson(player, jw);
			
			for (SpaceShip p : player.getShipsInRange()) {
				shipToJson(p,jw);
			}
				
			for (Bullet b : player.getBulletsInRange()) {
				bulletToJson(b,jw);
			}	

			for (Star g : player.getGarbagesInRange()) {
				energyToJson(g, jw);
			}

			for (Base b : player.getBasesInRange()) {
				baseToJson(b, jw);
			}

			jw.value(SERIALIZER_TYPE.GROUPS.ordinal()).value(stars.size());

			for (Group group : groups) {
				jw.value(group.getColor().ordinal()).
						value(group.getStars().size());
			}

			if(sendRank) {
				jw.value(SERIALIZER_TYPE.RANK.ordinal());

				for (int i = 0; i < ranksize; i++) {
					if (i >= players.size()) {
						jw.value(0).value(0).value(0);
					} else {
						jw.value(players.get(i).getId()).
								value(players.get(i).getName()).
								value(players.get(i).getPoints());
					}
				}
			}

			jw.endArray();
			out.write('\n');
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonWriter baseToJson(Base b, JsonWriter jw) throws IOException {
		jw.
		value(b.getType().ordinal()).
		value(b.getId()).
		value(tobigdecimal(b.getBody().getPosition().x)).
		value(tobigdecimal(b.getBody().getPosition().y)).
		value(tobigdecimal(b.getBody().getFixtureList().m_shape.m_radius)).
		value(tobigdecimal(b.getCurrentLife()*100/b.getTotalLife(),0,RoundingMode.DOWN));

		return jw;
	}


	private JsonWriter energyToJson(Star g, JsonWriter jw) throws IOException{
		jw.
		value(g.getType().ordinal()).
		value(g.getId()).
		value(tobigdecimal(g.getBody().getPosition().x)).
		value(tobigdecimal(g.getBody().getPosition().y)).
		value(tobigdecimal(g.getRadius())).
		value(g.getRange()).
		value(g.getGroup() != null ? g.getGroup().getColor().ordinal() : -1);

		return jw;
	}

	private JsonWriter bulletToJson(Bullet b, JsonWriter jw) throws IOException{
		jw.
		value(b.getType().ordinal()).
		value(b.getId()).
		value(tobigdecimal(b.getBody().getPosition().x)).
		value(tobigdecimal(b.getBody().getPosition().y)).
		value(tobigdecimal(b.getAngle())).
		value(b.getShip().getGroup().getColor().ordinal());

		return jw;
	}
	
	private JsonWriter shipToJson(SpaceShip ship, JsonWriter jw) throws IOException{
		jw.
		value(ship.getType().ordinal()).
		value(ship.getId()).
		value(tobigdecimal(ship.getBody().getPosition().x)).
		value(tobigdecimal(ship.getBody().getPosition().y)).
		value(tobigdecimal(ship.getAngle())).
		value(tobigdecimal(ship.getCurrentLife()*100/ship.getTotalLife(),0,RoundingMode.DOWN)).
		value(booleanToJson(ship.isbot())).
		value(booleanToJson(ship.isDamaged())).
		value(ship.getName()).
		value(ship.getPoints()).
		value(ship.getGroup().getColor().ordinal()).
		value(ship.getPower()).
		value(ship.getStarCaptureBulletReloadPercentage());
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
