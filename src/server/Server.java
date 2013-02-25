package server;

import server.ServerLogic;
import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import game.JSONObject;
import network.*;


public class Server {
	public static int			PORT		= 6667;
	public static int			BACKLOG		= 10; 			// the maximum number of pending connections on the socket.
	public final static int		VERSION		= 1;
	
	private boolean running = true;
	
	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;
	
	private int connectionCounter = 0;
	public Set<Connection> connections = Collections.newSetFromMap( new ConcurrentHashMap<Connection, Boolean>() );	
	
	private ServerLogic serverLogic;
	
	private Server( int port ) throws Exception {
		
		System.out.println( "Server::Server()" );
		System.out.println( "Using port "+port );

		// How to do multithreading? 1 thread for network stuff and rest for game logic?
		int threads = Math.max( Runtime.getRuntime().availableProcessors() - 1, 2 ); // number of threads for connections
		System.out.println( "Using "+threads+" thread"+(threads>1?"s":"") );
		
		group = AsynchronousChannelGroup.withThreadPool( Executors.newFixedThreadPool( threads ) );
		acceptor = AsynchronousServerSocketChannel.open( group );
		acceptor.bind( new InetSocketAddress( port ), BACKLOG );

		acceptor.accept(null, new CompletionHandler<AsynchronousSocketChannel,Void>() {
			public void completed(AsynchronousSocketChannel socket, Void att) {
				System.out.println( "New connection!" );
	    		// Don't accept new connections if game is running or there is enough players alreayd
				if ( !Server.this.serverLogic.isRunning() && Server.this.serverLogic.getPlayerCount() <= Server.this.serverLogic.getMaxPlayers() ) {
					Connection c = new Connection( ++connectionCounter, socket, Server.this );
					System.out.println( "New connection, #" + c.getId() );
					
					JSONObject settings = new JSONObject();
					settings.put("minPlayers", serverLogic.getMinPlayers());
					settings.put("maxPlayers", serverLogic.getMaxPlayers());
					settings.put("playersCount", serverLogic.getPlayerCount());
					settings.put("version", VERSION);
					settings.put("timeout", Connection.timeout);
					
					System.out.println("DEBUG: Sending serever settings: "+settings.toString());
					c.send(new JsonPacket(PacketType.SERVER_INFO, c.getId(), settings).toPacket());
					connections.add( c );
			    }
		    	  
		    	acceptor.accept(null, this); // accept the next connection
		    }
			public void failed(Throwable exc, Void att) {
				System.err.println( "Failed to receive connection: " + exc.getMessage() );
				exc.printStackTrace();
			}
		});

		serverLogic = new ServerLogic( this );
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
				Packet packet = c.receive();				
				
				// TODO: jos id väärä, huuda lujaa
				
				// Packet might be null if other thread has managed to poll it before this
				if ( packet == null ) continue;
				
				packet.toByteBuffer().putInt(2, c.getId());

				serverLogic.handlePacket( packet );
			}
		}
	}
	
	public void dropConnection( Connection c ) {
		System.out.println( "Dropping connection #" + c.getId() );
		
		
		
		if ( connections.contains( c )) {
			connections.remove( c );
			serverLogic.removePlayer( c.getId() );
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
		for ( Connection c : connections )
			c.send( packet );
	}

	public void sendToAllBut( int dontSend, Packet packet ) {
		for ( Connection c : connections )
			if (c.getId() != dontSend) c.send( packet );
	}

	public void reset() {
		for ( Connection c : connections ) {
			dropConnection( c );
		}
		
		connections.clear();
		serverLogic.reset();
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
