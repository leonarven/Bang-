package server;

import java.util.HashMap;
import java.util.LinkedList;

import network.*;
import game.Card;
import game.Character;
import game.Player;

public class Game {
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>(); // Synced with client
	private LinkedList<Card> discardPile = new LinkedList<Card>(); // Synced with client
	private LinkedList<Card> cardPile = new LinkedList<Card>(); // Client has no information about these
	
	private boolean isRunning = false;

	private final Server server;
	
	private int maxPlayers = 8;
	private int minPlayers = 1;
	
	public Game( Server server ) {
		this.server = server;
		
	}
	
	public void start() { 
		this.isRunning = true; 
		
		System.out.println( "Game is starting!" );

		// TODO: Remove clients who don't have player
		
		// TODO: READY-paketti pelaajille, tieto kunkin roolista
		
	}

	public void handlePacket( Packet packet, Connection connection ) {
		// Packet might be null if other thread has managed to poll it before this
		if ( packet == null ) return;

		try {
			
			// Returns null if there is no player with that id
			Player player = players.get( connection.getId() );
	
			if ( packet.getType() != PacketType.PING )
				System.out.println( "DEBUG: PacketType "+packet.getType().toChar()+" read from #"+connection.getId() );
	
			if ( player != null || packet.getType() == PacketType.CLIENT_INFO ) {
	
				switch(packet.getType()) {
				case MSG:
					StringPacket messagePacket = new StringPacket( packet );
					System.out.println( "DEBUG: Väitetty playerId "+messagePacket.getId() );
					if ( messagePacket.getId() == connection.getId() ) {
						System.out.println( "CHAT: " + player.getName() + ": " + messagePacket.getData() );
						server.sendToAll( packet );
					} else {
						System.out.println("DEBUG: Invalid player Id in MSG");
						// TODO: Jos lähettäjä ei ole kuka väittää
					}
					break;
				case READY:
					IntPacket readyPacket = new IntPacket( packet );
					System.out.println( "DEBUG: Väitetty playerId "+readyPacket.getId() );
					if (readyPacket.getData() == 0) player.setReady( false );
					else {
						player.setReady( true );
	
						// TODO: message(character_id):n persuteella valikoitu hahmo
						Character character = new Character("Unknown", 3);
						player.setCharacter( character );
	
						server.sendToAll( packet );
					}
					if ( readyToStart() ) {
						this.start(); // Start game when everyone is ready
					}
					break;
				case CLIENT_INFO:
					StringPacket clientInfo = new StringPacket( packet );
					System.out.println( "DEBUG: Väitetty playerId "+clientInfo.getId() );
	
					if ( player == null ) {
						// Uusi pelaaja
						
						if ( players.size() >= maxPlayers ) {
							System.out.println("DEBUG: Server is full - Dropping client");
							server.dropConnection( connection ); // FIXME: Server is full
							break;
						}
						player = new Player(connection.getId(), clientInfo.getData());
					}
	
					if ( player.getId() == connection.getId() ) {
						server.sendToAll( packet );
						for (Player p : players.values()) {
							connection.send(new StringPacket(PacketType.CLIENT_INFO, p.getId(), p.getName()).toPacket());
	
							if (p.isReady())
								connection.send(new IntPacket(PacketType.READY, p.getId(), 1).toPacket());
						}
						players.put( connection.getId(), player );
					} else {
						System.out.println("DEBUG: Invalid playerId in CLIENT_INFO");
						server.dropConnection( connection ); // FIXME: Server is full
					}
					break;
				case ERROR:
				default:
					break;
				}
			} else {
				server.dropConnection( connection ); // Client obviously doesn't know how to speak to server...
			}
		} catch(Exception e) {
			System.out.println("ERROR: Server:Game:handlePacket()");
			System.out.println(e.getMessage());
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

	public int getMinPlayers()
		{ return this.minPlayers; }
	
	public int getMaxPlayers() 
		{ return this.maxPlayers; }
}
