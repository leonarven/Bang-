package client;

import game.Card;
import game.Player;
import game.Character;
import game.PlayerType;

import java.util.HashMap;
import java.util.LinkedList;

import network.*;

public class Game {
	private final Client client;
	
	private HashMap<Integer, Player> players 	= new HashMap<Integer, Player>();
	private LinkedList<Card> discardPile 		= new LinkedList<Card>();
	
	private final int localPlayerId;

	public boolean isRunning = true;
	
	public Game( Client client, int localPlayerId ) {
		this.client = client;
		this.localPlayerId = localPlayerId;
	}
	
	public void SetReady(boolean ready) {
		this.players.get(localPlayerId).setReady( ready );
		
	}
	
	public void handlePacket( Packet packet ) {
		System.out.println( "DEBUG: PacketType "+packet.getType().toChar()+" read" );

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
		} else if ( packet.getType() == PacketType.READY ) {
			Message message = new Message( packet );
			if (message.getSenderId() == this.localPlayerId) {
				// TODO: Oma rooli
				players.get(localPlayerId).setType(PlayerType.fromString(message.getMessage()));
			} else {
				// TODO: Toiminnallisuus toisen pelaajan hahmon merkintään
				Character character = new Character("Unknown", 3);

				players.get(message.getSenderId()).setCharacter(character);
			}
		}
	}
	
	
	public Player getPlayer( int id ) { return players.get( id ); }
	public Player getPlayer()         { return players.get( localPlayerId ); }
	public int    getLocalPlayerId()  { return localPlayerId; }
}
