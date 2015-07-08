package com.haters.games.input;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

enum EventType {
	LEFT, RIGHT, UP, DOWN, FIRE, NEW_PLAYER
}

public class NetworkInputStream implements GameInputStream {



	private boolean[] eventsBitMap = new boolean[6];
	private SocketChannel channel;
	private StringBuilder eventbuffer = new StringBuilder();


	public NetworkInputStream() {
		for (int i = 0; i < eventsBitMap.length; i++) {
			eventsBitMap[i] = false;			
		}
	}
	
	@Override
	public boolean hasTurnLeftEvent() {
		return eventsBitMap[EventType.LEFT.ordinal()];
	}

	@Override
	public boolean hasTurnRightEvent() {
		return eventsBitMap[EventType.RIGHT.ordinal()];
	}

	@Override
	public boolean hasAccelerationEvent() {
		return eventsBitMap[EventType.UP.ordinal()];
	}

	@Override
	public boolean hasBreakEvent() {
		return eventsBitMap[EventType.DOWN.ordinal()];
	}

	@Override
	public boolean hasFireEvent() {
		return eventsBitMap[EventType.FIRE.ordinal()];
	}

	@Override
	public boolean hasNewPlayerEvent() {
		return eventsBitMap[EventType.NEW_PLAYER.ordinal()];
	}

	@Override
	public void processEvents() {
		eventsBitMap[EventType.NEW_PLAYER.ordinal()] = false;
		try {
			String[] inputs = checkInput();
			for (String input : inputs) {
				JsonElement json =  new JsonParser().parse(input);

				String event = json.getAsJsonObject().get("event").getAsString();

				Boolean pressed = json.getAsJsonObject().get("pressed") != null? json.getAsJsonObject().get("pressed").getAsBoolean() : true;

				eventsBitMap[EventType.valueOf(event).ordinal()] = pressed;
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
