package com.haters.games.output;

import com.haters.games.physics.PolygonSpaceShip;
import com.haters.games.physics.SpaceShip;
import com.haters.games.physics.SpaceWorld;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;

public class NetworkOutputStream {
	private PrintWriter out;
	private GameSerializer serializer = new GameSerializer();


	private Writer getWriter() {
		
		try {
			if(out == null){
				out = new PrintWriter(new Socket("127.0.0.1", 1337).getOutputStream(), false);
			}	
			return out;

		} catch ( IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void streamGame(SpaceWorld spaceWorld, List<SpaceShip> bots, List<SpaceShip> players) {
		for (SpaceShip player : players){
            serializer.serializeForPlayer(player,bots,players,getWriter());
        }
	}
}
