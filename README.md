# Space fight backend engine
2D Massively multiplayer online game backend written in java. Communicates with the frontend (node) using non blocking sockets and uses Box2D for physics simulation.

# Quick start
The java application depends on som jbox2d dependencies.

First, generate for the jar files for jbox2d-library and jbox2d-serialization. Commands below.
```
cd [project folder]
git clone git@github.com:jbox2d/jbox2d.git
cd [project folder]/jbox2d/jbox2d-library
mvn install
cd [project folder]/jbox2d/jbox2d-serialization
mvn install
```

Second, you need to run SpaceFightMain main class.
```
mvn compile exec:java -Dexec.mainClass="com.haters.games.SpaceFightMain" 
```

You should see an output like below.
```
java.net.ConnectException: Connection refused
	at sun.nio.ch.SocketChannelImpl.checkConnect(Native Method)
	at sun.nio.ch.SocketChannelImpl.finishConnect(SocketChannelImpl.java:717)
	at com.haters.games.input.NetworkInputStream.connect(NetworkInputStream.java:174)
	at com.haters.games.input.NetworkInputStream.checkInput(NetworkInputStream.java:182)
	at com.haters.games.input.NetworkInputStream.processEvents(NetworkInputStream.java:116)
	at com.haters.games.GameController.run(GameController.java:69)
	at java.lang.Thread.run(Thread.java:748)
frame rate: 60.0
still connecting
still connecting
```

This means the game is running in background, but the is no space-fight-web (nodejs application)
to connect to.

Now you should get to  it and run space-fight-web

```
cd [project folder]
git clone git@github.com:fernandolvsouza/space-fight-web.git
cd space-fight-web
npm install
node game.js
```  

Now, if you go back to SpaceFightMain console output you should see no exceptions, 
only the frame rate of the game. If too many physics calculations are being processed by the game
 the frame rate will decrease, and the experience might get  worst.
```
still connecting
frame rate: 55.840794
frame rate: 55.69177
frame rate: 55.316105
frame rate: 55.83015
```

Now have fun accessing [http://127.0.0.1](http://127.0.0.1)

