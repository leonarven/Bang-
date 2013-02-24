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

	private boolean isRunning = false;
	
	public Game( Client client, int localPlayerId ) {
		this.client = client;
		this.localPlayerId = localPlayerId;
	}
	
	public void SetReady(boolean ready) {
		if ( isRunning = false ) {
			this.client.send(new IntPacket( PacketType.READY, localPlayerId, ready?1:0 ).toPacket());
		}
	}
	
	public void handlePacket( Packet packet ) {
		System.out.println( "DEBUG: PacketType "+packet.getType().toChar()+" read" );

		try {
			if ( !isRunning ) {
				// Vin rhe:
				if ( packet.getType() == PacketType.ERROR ) {
					IntPacket errorPacket = new IntPacket(packet);
					if (ErrorCode.fromInt(errorPacket.getData()) == ErrorCode.INVALID_USERNAME && 
							!players.containsKey(localPlayerId)) {
						// TODO: Mitä tehdä jos client-info ei kelvannut?
					}
				// Client info: uusi pelaaja!:
				} else if ( packet.getType() == PacketType.CLIENT_INFO) {
					Player player = ClientInfo.createPlayer( packet );
					players.put( player.getId(), player );
					System.out.println(player.getName() + " joined");
				// Viesti chatissä!:
				} else if ( packet.getType() == PacketType.MSG ) {
					StringPacket message = new StringPacket( packet );
					if (message.getId() == this.localPlayerId) {
						// TODO: Oma viesti
						System.out.println( "CHAT: <self> " + message.getData());
					} else {
						System.out.println( "CHAT: <" + players.get(message.getData()).getName() + "(#" + message.getId() + ")> " + message.getData());
					}
				// Pelaaja kertoi olevansa valmis!:
				} else if ( packet.getType() == PacketType.READY ) {
					IntPacket ready = new IntPacket( packet );
					players.get(ready.getId()).setReady(ready.getData() !=0 );
					
					// Tarkista onko kaikki pelaajat valmiina jos pelaaja ei perunut valmiuttaan
					if ( ready.getData() != 0 ) {
						boolean allReady = true;
						for (Player p : players.values()) {
							if (!p.isReady()) {
								allReady = false;
								break;
							}
						}
						
						if ( allReady ) {
							// TODO aloita peli
						}
					}
				}
			}
		} catch ( Exception e ) {
			System.err.println( e );
		}
	}
	
	
	public Player getPlayer( int id ) { return players.get( id ); }
	public Player getPlayer()         { return players.get( localPlayerId ); }
	public int    getLocalPlayerId()  { return localPlayerId; }
}
