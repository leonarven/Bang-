package server;

import java.util.HashMap;
import java.util.LinkedList;

import network.ClientInfo;
import network.Message;
import network.Packet;
import network.PacketType;
import game.Card;
import game.Player;

public class Game {
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	private LinkedList<Card> discardPile = new LinkedList<Card>();
	
	private boolean isRunning = false;

	private final Server server;
	
	private int maxPlayers = 8;
	private int minPlayers = 1;
	
	public Game( Server server ) {
		this.server = server;
		
	}
	
	public void start() { 
		this.isRunning = true; 
	
		
		
	}

	public void handlePacket( Packet packet, Connection connection ) {
		// Packet might be null if other thread has managed to poll it before this
		if ( packet == null ) return;
		
		// Returns null if there is no player with that id
		Player player = players.get( connection.getId() );

		if ( this.isRunning() ) {
			


		// Game has not started: player is in lobby -> wait for everyone to get ready
		} else if ( player != null ) {
			if ( packet.getType() == PacketType.MSG ) {
				Message message = new Message( packet );
				if ( message.getSenderId() == connection.getId() ) {
					System.out.println( "CHAT " + player.getName() + ": " + message.getMessage() );
					server.sendToAll( packet );
				}
			} else if ( packet.getType() == PacketType.READY ) {
				player.setReady( !player.isReady() );
				if ( readyToStart() ) {
					this.start(); // Start game when everyone is ready
				}
			}
		// Player is not in game -> wait for client info packet...
		} else if ( packet.getType() == PacketType.CLIENT_INFO ) {
			if ( players.size() >= maxPlayers ) {
				server.dropConnection( connection ); // Server is full
			}

			// Add player to game if clientinfo packet was correct
			player = ClientInfo.createPlayer( packet );
			if ( player.getId() == connection.getId() ) {
				players.put( connection.getId(), player );
				server.sendToAll( packet );
			}
			
		// Client didn't send the client info packet
		} else {
			server.dropConnection( connection ); // Client obviously doesn't know how to speak to server...
		}
	}
	
	// Return true if there is enough players and everyone is ready
	public boolean readyToStart() {
		if ( players.size() < minPlayers )
			return false;
		
		for ( Player p : players.values() ) {
			if ( !p.isReady() )
				return false;
		}
		
		return true;
	}
	
	public void reset() {
		players.clear();
		
		isRunning = false;
	}
	
	public boolean isRunning() 
		{ return this.isRunning; }

	public void addPlayer( Player player ) {
		players.put( player.getId(), player );
	}
	
	public void removePlayer( int id ) {
		if ( !isRunning() ) {
			if ( players.remove( id ) != null ) {
				// TODO: send message to other clients			
			}
		} else {
			// TODO: What to do if player leaves while game is running?
		}
	}
	
	public Player getPlayer( int id ) 
		{ return players.get( id ); }
	
	public boolean hasPlayer( int id )
		{ return players.containsKey( id ); }
	
	public int getPlayerCount() 
		{ return players.size(); }

	public int getMaxPlayers() 
		{ return maxPlayers; }
}
