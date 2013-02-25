package client;

import network.ErrorCode;
import network.IntPacket;
import network.Packet;
import network.PacketType;
import network.StringPacket;
import game.*;

public class ClientLogic extends Game {
	private final Client client;
	private final int localPlayerId;
	
	public ClientLogic(Client client, int localPlayerId) {
		this.client 		= client;
		this.localPlayerId 	= localPlayerId;
	}

	public int getLocalPlayerId()
		{ return localPlayerId; }
	
	@Override
	public void handlePacket(Packet packet) {
		System.out.println( "DEBUG: PacketType "+packet.getType().toChar()+" read" );

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
				
					System.out.println( "CHAT: <self:" + players.get(message.getId()).getName() + " (#" + message.getId() + ")> " + message.getData());

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

	public void SetReady(boolean ready) {
		if ( isRunning = false ) {
			this.client.send(new IntPacket( PacketType.READY, localPlayerId, ready ).toPacket());
		}
	}

	@Override
	protected void doStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doReset() {
		// TODO Auto-generated method stub
		
	}

}
