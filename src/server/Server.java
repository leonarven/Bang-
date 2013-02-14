package server;

import server.Game;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import network.Packet;
import network.ServerInfo;

public class Server {
	public static int			PORT		= 6667;
	public static int			BACKLOG		= 10; 			// the maximum number of pending connections on the socket.
	
	private boolean running = true;
	
	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;
	
	private int connectionCounter = 0;
	private Set<Connection> connections = Collections.newSetFromMap( new ConcurrentHashMap<Connection, Boolean>() );	
	
	private Game game;
	
	private Server( int port ) throws Exception {

		System.out.println( "Server::Server()" );
		System.out.println( "Using port "+port );

		// How to do multithreading? 1 thread for network stuff and rest for game logic?
		int threads = Math.max( Runtime.getRuntime().availableProcessors() - 1, 1 ); // number of threads for connections
		System.out.println( "Using "+threads+" thread"+(threads>1?"s":"") );
		
		group = AsynchronousChannelGroup.withThreadPool( Executors.newFixedThreadPool( threads ) );
		acceptor = AsynchronousServerSocketChannel.open( group );
		acceptor.bind( new InetSocketAddress( port ), BACKLOG );

		acceptor.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {
			public void completed(AsynchronousSocketChannel socket, Void att) {
				System.out.println( "New connection!" );
	    		// Don't accept new connections if game is running or there is enough players alreayd
				if ( !Server.this.game.isRunning() && Server.this.game.getPlayerCount() <= Server.this.game.getMaxPlayers() ) {
					Connection c = new Connection( ++connectionCounter, socket, Server.this );
					System.out.println( "New connection, #" + c.getId() );
					c.send((new ServerInfo(c.getId(), game.getMinPlayers(), game.getMaxPlayers())).toPacket());
					connections.add( c );
			    }
		    	  
		    	acceptor.accept(null, this); // accept the next connection
		    }
			public void failed(Throwable exc, Void att) {
				System.err.println( "Failed to receive connection: " + exc.getMessage() );
				exc.printStackTrace();
			}
		});
		
		game = new Game( this );
	}

	private void serverLoop() {
		// if game logic has more than 1 thread this function should be redone
		for ( Connection c : connections ) {
			if ( !c.isConnected() ) {
				dropConnection( c );
				continue;
			}
			
			// https://en.wikipedia.org/wiki/Producer-consumer_problem	
			// Client should send keep-alive messages. receive has timeout value.
			if ( c.hasReceivedData() ) {
				System.out.println( "DEBUG: c(#"+c.getId()+").hasReceivedData()" );
				game.handlePacket( c.receive(), c );
				
			}
		}
	}
	
	public void dropConnection( Connection c ) {
		System.out.println( "Dropping connection #" + c.getId() );
		
		if ( connections.contains( c )) {
			connections.remove( c );
			game.removePlayer( c.getId() );
		}

		if ( c.isConnected() ) {	
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void sendToAll( Packet packet ) {
		for ( Connection c : connections ) {
			c.send( packet );
		}
	}
	
	public void reset() {
		for ( Connection c : connections ) {
			dropConnection( c );
		}
		
		connections.clear();
		game.reset();
	}
	
	public static void main(String[] args) {
		System.out.println( "BEGIN" );
		System.out.print( "Reading config file ... " );
		// TODO: Read settings from config file...
		System.out.println( "DONE" );
		
		try {
			System.out.println( "Trying to start server ..." );
			Server server = new Server( PORT);
			System.out.println( "Starting the loop" );
			while ( server.running ) {
				server.serverLoop();
			}
		} catch (Exception e) {
			System.out.println( " ERROR" );
			System.out.print("Fatal exception at Server::main(): ");
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
