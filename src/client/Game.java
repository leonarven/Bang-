package client;

import game.Card;
import game.Player;

import java.util.HashMap;
import java.util.LinkedList;

import network.*;

public class Game {
	private HashMap<Integer, Player> players 	= new HashMap<Integer, Player>();
	private LinkedList<Card> discardPile 		= new LinkedList<Card>();
	
	private final int localPlayerId;

	public boolean isRunning = true;
	
	public Game( int localPlayerId ) {
		this.localPlayerId = localPlayerId;
	}
	
	public void handlePacket( Packet packet ) {
		if ( packet.getType() == PacketType.CLIENT_INFO) {
			Player player = ClientInfo.createPlayer( packet );
			players.put( player.getId(), player );
			System.out.println(player.getName() + " joined");
		} else if ( packet.getType() == PacketType.MSG ) {
			Message message = new Message( packet );
			System.out.println( "CHAT: " + players.get(message.getSenderId()) + ": " + message.getMessage());
		}
	}
	
	
	public Player getPlayer( int id ) {
		return players.get( id );
	}
	
	public int getLocalPlayerId() {
		return localPlayerId;
	}

}
