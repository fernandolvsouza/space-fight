package com.haters.games.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;

import com.haters.games.physics.SpaceShip;
import com.haters.games.physics.SpaceWorld;

public class NetworkOutputStream {
	private PrintWriter out;
	private GameSerializer serializer = new GameSerializer();


	private Writer getWriter() throws IOException {
		

		if(out == null || out.checkError()){
			System.out.println("reconnecting ...");
			out = new PrintWriter(new Socket("127.0.0.1", 1337).getOutputStream(), false);
		}

		return out;


	}


	public void streamGame(SpaceWorld spaceWorld, List<SpaceShip> bots, List<SpaceShip> players,boolean sendRanking,int ranksize) {
		try {
			for (SpaceShip player : players){
				serializer.serializeForPlayer(player,bots,players,sendRanking,ranksize,getWriter());
			}
		} catch ( IOException e) {
			if(out != null)
				out.close();
			e.printStackTrace();
		}
	}
}
