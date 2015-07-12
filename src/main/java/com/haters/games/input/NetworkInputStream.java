package com.haters.games.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
	LEFT, RIGHT, UP, DOWN, FIRE, NEW_PLAYER
}

public class NetworkInputStream implements GameInputStream {

	private Map<Integer,boolean[]> eventsByPlayers = new HashMap<Integer, boolean[]>();
	private List<Integer> newPlayers = new ArrayList<Integer>();
	private SocketChannel channel;
	private StringBuilder eventbuffer = new StringBuilder();


	public NetworkInputStream() {

	}
	
	@Override
	public boolean hasTurnLeftEvent(SpaceShip player) {
		return eventsByPlayers.get(player.getId())[EventType.LEFT.ordinal()];
	}

	@Override
	public boolean hasTurnRightEvent(SpaceShip player) {
		return eventsByPlayers.get(player.getId())[EventType.RIGHT.ordinal()];
	}

	@Override
	public boolean hasAccelerationEvent(SpaceShip player) {
		return eventsByPlayers.get(player.getId())[EventType.UP.ordinal()];
	}

	@Override
	public boolean hasBreakEvent(SpaceShip player) {
		return eventsByPlayers.get(player.getId())[EventType.DOWN.ordinal()];
	}

	@Override
	public boolean hasFireEvent(SpaceShip player) {
		return eventsByPlayers.get(player.getId())[EventType.FIRE.ordinal()];
	}

	@Override
	public boolean hasNewPlayerEvent() {
		return !newPlayers.isEmpty();
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
	public void processEvents() {
		try {
			String[] inputs = checkInput();
			for (String input : inputs) {
				JsonElement json =  new JsonParser().parse(input);

				String event = json.getAsJsonObject().get("event").getAsString();
				JsonElement payload = json.getAsJsonObject().get("payload");
				Integer userId = payload.getAsJsonObject().get("user").getAsJsonObject().get("id").getAsInt();
				if(EventType.valueOf(event) != EventType.NEW_PLAYER){
					Boolean pressed = payload.getAsJsonObject().get("pressed").getAsBoolean();
					eventsByPlayers.get(userId)[EventType.valueOf(event).ordinal()] = pressed;
				}else{
					newPlayers.add(userId);
					boolean[] bitmap = {false,false,false,false,false};
					eventsByPlayers.put(userId,bitmap);
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
					System.out.println(events.get(events.size() - 1));
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
