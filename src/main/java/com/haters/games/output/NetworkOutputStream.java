package com.haters.games.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

public class NetworkOutputStream {
	PrintWriter out;
	

	public Writer getWriter() {
		
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
}
