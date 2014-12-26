package com.haters.games.input;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NetworkInputStream implements GameInputStream {

	private boolean[] eventsBitMap = new boolean[5];
	SocketChannel channel;

	public NetworkInputStream() {
		for (int i = 0; i < eventsBitMap.length; i++) {
			eventsBitMap[i] = false;			
		}
	}
	
	@Override
	public boolean hasTurnLeftEvent() {
		return eventsBitMap[0];
	}

	@Override
	public boolean hasTurnRightEvent() {
		return eventsBitMap[1];
	}

	@Override
	public boolean hasAccelerationEvent() {
		return eventsBitMap[2];
	}

	@Override
	public boolean hasBreakEvent() {
		return eventsBitMap[3];
	}

	@Override
	public boolean hasFireEvent() {
		return eventsBitMap[4];
	}

	@Override
	public void processEvents() {
		
		try {
			String[] inputs = checkInput();
			for (String input : inputs) {
				switch (input) {
				case "LEFT_1":
					eventsBitMap[0] = true;
					break;
				case "RIGHT_1":
					eventsBitMap[1] = true;
					break;
				case "UP_1":
					eventsBitMap[2] = true;
					break;
				case "DOWN_1":
					eventsBitMap[3] = true;
					break;
				case "FIRE_1":
					eventsBitMap[4] = true;
					break;
					
				case "LEFT_0":
					eventsBitMap[0] = false;
					break;
				case "RIGHT_0":
					eventsBitMap[1] = false;
					break;
				case "UP_0":
					eventsBitMap[2] = false;
					break;
				case "DOWN_0":
					eventsBitMap[3] = false;
					break;
				case "FIRE_0":
					eventsBitMap[4] = false;
					break;
				default:
					break;
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

		ByteBuffer buffer = ByteBuffer.allocate(20);

		String message = "";
		while (channel.read(buffer) > 0) {
			buffer.flip();
			message += Charset.defaultCharset().decode(buffer);
		}

		System.out.println(message);
		return message.split("\n");
	}
}
