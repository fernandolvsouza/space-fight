package com.haters.games.input;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haters.games.physics.SpaceShip;

enum EventType {
	LEFT, RIGHT, UP, DOWN, FIRE, MOUSE_MOVE, NEW_PLAYER, REMOVE_PLAYER, BE_BORN, DIE
}

public class NetworkInputStream implements GameInputStream {

	private Map<Integer,Object[]> inputStateByPlayers = new HashMap<Integer, Object[]>();
	private List<Integer> newPlayers = new ArrayList<Integer>();
	private List<Integer> removePlayers = new ArrayList<Integer>();
	private List<Object[]> bebornPlayers = new ArrayList<Object[]>();
	private SocketChannel channel;
	private StringBuilder eventbuffer = new StringBuilder();


	public NetworkInputStream() {

	}

	@Override
	public float[] getMouseMoveEvent(SpaceShip player) {
		return (float[])inputStateByPlayers.get(player.getId())[EventType.MOUSE_MOVE.ordinal()];
	}

	@Override
	public boolean hasLeftEvent(SpaceShip player) {
		return (Boolean)inputStateByPlayers.get(player.getId())[EventType.LEFT.ordinal()];
	}

	@Override
	public boolean hasRightEvent(SpaceShip player) {
		return (Boolean)inputStateByPlayers.get(player.getId())[EventType.RIGHT.ordinal()];
	}

	@Override
	public boolean hasUpEvent(SpaceShip player) {
		return (Boolean)inputStateByPlayers.get(player.getId())[EventType.UP.ordinal()];
	}

	@Override
	public boolean hasDownEvent(SpaceShip player) {
		return (Boolean)inputStateByPlayers.get(player.getId())[EventType.DOWN.ordinal()];
	}

	@Override
	public boolean hasFireEvent(SpaceShip player) {
		return (Boolean)inputStateByPlayers.get(player.getId())[EventType.FIRE.ordinal()];
	}

	@Override
	public boolean hasNewPlayerEvent() {
		return !newPlayers.isEmpty();
	}

	@Override
	public boolean hasRemovePlayerEvent(SpaceShip player) {
		return removePlayers.contains(player.getId());
	}
	
	@Override
	public Object[] getBeBornEvent(SpaceShip player) {
		for (Object[] born : bebornPlayers) {
			Integer id = (Integer) born[0];
			if(player.getId() == id ){
				return born;
			}
		}
		return null;
	}

	@Override
	public List<Integer> getNewPlayers() {
		return newPlayers;
	}

	@Override
	public void eraseNewPlayersEvents() {
		newPlayers.clear();
	}

	@Override
	public void eraseRemovePlayersEvents() {
		removePlayers.clear();
	}
	
	@Override
	public void eraseBeBornPlayersEvents() {
		bebornPlayers.clear();
	}


	@Override
	public void processEvents() {
		try {
			String[] inputs = checkInput();
			for (String input : inputs) {
				JsonElement json =  new JsonParser().parse(input);
				JsonArray array = json.getAsJsonArray();

				Integer userId = array.get(0).getAsInt();
				Integer event = array.get(1).getAsInt();

				
				if (event == EventType.NEW_PLAYER.ordinal()) {
					newPlayers.add(userId);
					Object[] bitmap = {false,false,false,false,false,null};
					inputStateByPlayers.put(userId, bitmap);
				}else if (event == EventType.REMOVE_PLAYER.ordinal()) {
					removePlayers.add(userId);
					inputStateByPlayers.remove(userId);
				}else if (event == EventType.BE_BORN.ordinal()) {
					String name = array.get(2).getAsString();
					bebornPlayers.add(new Object[]{userId,name});
				}else if (event == EventType.MOUSE_MOVE.ordinal()){
					if(inputStateByPlayers.containsKey(userId)) {
						float x = array.get(2).getAsFloat();
						float y = array.get(3).getAsFloat();
						inputStateByPlayers.get(userId)[event] = new float[]{x,y};
					}
				}else {
					if(inputStateByPlayers.containsKey(userId)) {
						Boolean pressed = array.get(2).getAsInt() == 1 ;
						inputStateByPlayers.get(userId)[event] = pressed;
					}
				}
			}			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	private void connect() throws IOException{
		channel = SocketChannel.open();
		 
        // we open this channel in non blocking mode
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", 1234));

        while (!channel.finishConnect()) {
            System.out.println("still connecting");
        }
	}
	
	private String[] checkInput() throws IOException {
		if(channel == null){
			connect();
		}

		List<String> events = new ArrayList<String>();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while(channel.read(buffer) > 0)
		{
			buffer.flip();

			for (int i = 0; i < buffer.limit(); i++)
			{
				char b = (char) buffer.get();
				if(b == '\n') {
					events.add(eventbuffer.toString());
					//System.out.println(events.get(events.size() - 1));
					eventbuffer.setLength(0);
				}else {
					eventbuffer.append(b);
				}
			}

			//buffer.clear(); // do something with the data and clear/compact it.
		}

		return events.toArray(new String[events.size()]);
	}
}
