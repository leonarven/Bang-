package client;

import game.*;

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
		System.out.println( "DEBUG: Olen pelaaja "+this.localPlayerId );

		try {
			switch(packet.getType()) {
			case ERROR:
				IntPacket errorPacket = new IntPacket(packet);
				if (ErrorCode.fromInt(errorPacket.getData()) == ErrorCode.INVALID_USERNAME && 
						!players.containsKey(localPlayerId)) {
					// TODO: Mitä tehdä jos client-info ei kelvannut?
				}
				break;
			case CLIENT_INFO:
				StringPacket clientInfo = new StringPacket(packet);
				players.put( clientInfo.getId(), new Player(clientInfo.getId(), clientInfo.getData()) );
				System.out.println(clientInfo.getData() + " joined");
				break;
			case MSG:
				StringPacket message = new StringPacket( packet );

				if (message.getId() == this.localPlayerId) {
					System.out.println( "DEBUG: CHAT: Oma viesti: ");
					System.out.print( "CHAT: <" + players.get(message.getId()).getName());
					System.out.println( " (#" + message.getId() + ")> " + message.getData());

					System.out.println(players.get(this.localPlayerId).getName().length());
					System.out.println(message.getData().length());
				} else {

					System.out.println( "CHAT: <" + players.get(message.getId()).getName() + "(#" + message.getId() + ")> " + message.getData());
				}
				
				break;
			case READY:
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
				break;
			default:
				break;
			}
		} catch ( Exception e ) {
			System.err.println( e );
		}
	}
	
	
	public Player getPlayer( int id ) { return players.get( id ); }
	public Player getPlayer()         { return players.get( localPlayerId ); }
	public int    getLocalPlayerId()  { return localPlayerId; }
}
