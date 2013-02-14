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
		System.out.println( "DEBUG: PacketType "+packet.getType().toChar()+" readed" );

		if ( packet.getType() == PacketType.CLIENT_INFO) {
			Player player = ClientInfo.createPlayer( packet );
			players.put( player.getId(), player );
			System.out.println(player.getName() + " joined");
		} else if ( packet.getType() == PacketType.MSG ) {
			Message message = new Message( packet );
			if (message.getSenderId() == this.localPlayerId) {
				// TODO: Oma viesti
				System.out.println( "CHAT: <self> " + message.getMessage());
			} else {
				System.out.println( "CHAT: <" + players.get(message.getSenderId()).getName() + "(#" + message.getSenderId() + ")> " + message.getMessage());
			}
		}
	}
	
	
	public Player getPlayer( int id ) {
		return players.get( id );
	}
	
	public int getLocalPlayerId() {
		return localPlayerId;
	}

}
