package server;

import game.GameContext;
import game.Player;

import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import network.Packet;

public class Server {
	public static int			PORT		= 6667;
	public static int			BACKLOG		= 10; 			// the maximum number of pending connections on the socket.
	
	private boolean running = true;
	
	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;
	
	private int connectionCounter = 0;
	private Set<Connection> connections = Collections.newSetFromMap( new ConcurrentHashMap<Connection, Boolean>() );
	
	GameContext game = new GameContext();
	
	private Server( int port ) throws Exception {
		group = AsynchronousChannelGroup.withThreadPool( Executors.newSingleThreadExecutor() );
		acceptor = AsynchronousServerSocketChannel.open( group );
		acceptor.bind( new InetSocketAddress( port ), BACKLOG );
	}
	
	private void startAcceptConnection() {
		 acceptor.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {
		      public void completed(AsynchronousSocketChannel socket, Void att) {
		    	  handleNewConnection( new Connection( ++connectionCounter, socket, Server.this ) );
		          acceptor.accept(null, this); // accept the next connection
		      }
		      public void failed(Throwable exc, Void att) {
		    	  System.err.println( exc.getMessage() );
		    	  exc.printStackTrace( System.err );
		      }
		  });
	}
	
	private void handleNewConnection( Connection connection ) {
		// Don't accept new connections after game has started.
		if ( !game.isRunning() ) {
			connections.add( connection );
			game.addPlayer( new Player( connection.getId() ));
			// TODO: Send info to clients?
		} else {
			// TODO: don't allow connection

		}
	}
	
	private void serverLoop() {
		for ( Connection c : connections ) {
			if ( !c.isConnected() ) {
				dropConnection( c );
				continue;
			}
			
			// Client should send keep-alive messages. receive has timeout value.
			if ( c.hasReceivedData() ) {
				Packet packet = c.receive();
				// TODO: forward packet to game or lobby class
			}
		}
	}
	
	public void dropConnection( Connection c ) {
		System.out.println( "Dropping connection #" + c.getId() );
		
		if ( connections.contains( c )) {
			connections.remove( c );
		}
		
		// c.Send( "Disconnected" ); ???

		// TODO: Send message to other clients?
	}
	
	public static void main(String[] args) {
		
		// TODO: Read settings from config file...
		
		try {
			Server server = new Server( PORT);
			server.startAcceptConnection();

			while ( server.running ) {
				server.serverLoop();
			}
		} catch (Exception e) {
			System.out.print("Fatal exception at Server::main(): ");
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
