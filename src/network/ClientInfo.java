package network;

import game.Player;

import java.nio.ByteBuffer;

// Extends message?
public class ClientInfo {
	public ClientInfo(int id, String name) {
		
	}

	static public Player createPlayer( Packet packet ) {
		if ( packet.getType() != PacketType.CLIENT_INFO ) {
			return null;
		}
		
		ByteBuffer buffer = packet.toByteBuffer();
		return new Player( buffer.getInt( 2 ), new String( buffer.array(), 6, buffer.limit() - 6 ));
	}


}
