package server;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.Executors;


public class Server {
	public static final int PORT = 6667;

	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;


	Server(int port) throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
		acceptor = AsynchronousServerSocketChannel.open(group);
		acceptor.bind(new InetSocketAddress(port));
	
	}
	
	void ServerLoop() throws Exception {
		AsynchronousSocketChannel socket = acceptor.accept().get();
	    System.out.println("Accepted " + socket.getRemoteAddress().toString());
	    socket.close();
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
