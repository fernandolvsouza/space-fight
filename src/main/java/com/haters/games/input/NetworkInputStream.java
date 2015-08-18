package com.haters.games.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haters.games.physics.PolygonSpaceShip;
import com.haters.games.physics.SpaceShip;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum EventType {
	LEFT, RIGHT, UP, DOWN, FIRE, MOUSE_MOVE, NEW_PLAYER, REMOVE_PLAYER
}

public class NetworkInputStream implements GameInputStream {

	private Map<Integer,Object[]> inputStateByPlayers = new HashMap<Integer, Object[]>();
	private List<Integer> newPlayers = new ArrayList<Integer>();
	private List<Integer> removePlayers = new ArrayList<Integer>();
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
	public boolean hasRemovePlayerEvent() {
		return !removePlayers.isEmpty();
	}

	@Override
	public List<Integer> getNewPlayers() {
		return newPlayers;
	}

	@Override
	public List<Integer> getRemovePlayers() {
		return removePlayers;
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
	public void processEvents() {
		try {
			String[] inputs = checkInput();
			for (String input : inputs) {
				JsonElement json =  new JsonParser().parse(input);

				String event = json.getAsJsonObject().get("event").getAsString();
				JsonElement payload = json.getAsJsonObject().get("payload");
				Integer userId = payload.getAsJsonObject().get("user").getAsJsonObject().get("id").getAsInt();
				if (EventType.valueOf(event) == EventType.NEW_PLAYER) {
					newPlayers.add(userId);
					Object[] bitmap = {false,false,false,false,false,null};
					inputStateByPlayers.put(userId, bitmap);
				}else if (EventType.valueOf(event) == EventType.REMOVE_PLAYER) {
					removePlayers.add(userId);
					inputStateByPlayers.remove(userId);
				}else if (EventType.valueOf(event) == EventType.MOUSE_MOVE){
					if(inputStateByPlayers.containsKey(userId)) {
						float x = payload.getAsJsonObject().get("x").getAsFloat();
						float y = payload.getAsJsonObject().get("y").getAsFloat();
						inputStateByPlayers.get(userId)[EventType.valueOf(event).ordinal()] = new float[]{x,y};
					}
				}else {
					if(inputStateByPlayers.containsKey(userId)) {
						Boolean pressed = payload.getAsJsonObject().get("pressed").getAsBoolean();
						inputStateByPlayers.get(userId)[EventType.valueOf(event).ordinal()] = pressed;
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
