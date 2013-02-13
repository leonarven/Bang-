package network;

import game.Player;

import java.nio.ByteBuffer;


// Packet format:
//0: 	char 	= PacketType
//2: 	int 	= from 
//4... 	String 	= message


// Extends message?
public class ClientInfo extends Message {
	public ClientInfo(int id, String name) {
		super( PacketType.CLIENT_INFO, id, name );
	}

	static public Player createPlayer( Packet packet ) {
		if ( packet.getType() != PacketType.CLIENT_INFO ) {
			return null;
		}
		
		ByteBuffer buffer = packet.toByteBuffer();
		return new Player( buffer.getInt( 2 ), new String( buffer.array(), 6, buffer.limit() - 6 ));
	}


}
