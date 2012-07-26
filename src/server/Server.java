package server;

import java.net.*;
import java.nio.channels.*;
import java.util.Vector;
import java.util.concurrent.Executors;


public class Server {
	public static final int PORT = 6667;

	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;
	
	Vector<Connection> clients;


	Server(int port) throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
		acceptor = AsynchronousServerSocketChannel.open(group);
		acceptor.bind(new InetSocketAddress(port));
	
	}
	
	void ServerLoop() throws Exception {
		clients.add(new Connection(acceptor.accept().get()));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting server");
		
		try {
			Server game = new Server(PORT);
			while (true)
				game.ServerLoop();
			
		} catch (Exception e) {
			System.out.println("Fatal exection: " + e.getMessage());
		}
		
	}

}
