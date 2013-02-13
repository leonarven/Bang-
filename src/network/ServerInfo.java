package network;

import java.nio.ByteBuffer;

//Packet format:
//0: 	char 	= PacketType
//2: 	int 	= version
//6 	int 	= client id
//10 	int 	= maxPlayers
//14 	int 	= minPlayers

public class ServerInfo {
	public static final int VERSION = 1;
	
	private int id;
	private int minPlayers;
	private int maxPlayers;
	
	public ServerInfo( int id, int minPlayers, int maxPlayers ) {
		this.id = id;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
	}
	
	public ServerInfo( Packet packet ) {		
		ByteBuffer buffer = packet.toByteBuffer();
		
		//TODO:
		assert buffer.limit() == 14;
		
		buffer.position( 2 );
		int version 	= buffer.getInt();
		this.id 		= buffer.getInt();
		this.minPlayers = buffer.getInt();
		this.maxPlayers = buffer.getInt();
		
		assert version == VERSION;
		
		if ( version < VERSION ) {
			// TODO: error here. Throw something?
		}
	}
	
	public Packet toPacket() {
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE + 4 * Integer.SIZE );
		buffer.putChar( PacketType.SERVER_INFO.toChar() );
		buffer.putInt( VERSION );
		buffer.putInt( id );
		buffer.putInt( minPlayers );
		buffer.putInt( maxPlayers );
		return new Packet( PacketType.SERVER_INFO, buffer );
	}
	
	public int getId()
		{ return this.id; }
	
	public int getMinPlayers() 
		{ return this.minPlayers; }
	
	public int getMaxPlayers()	
		{ return this.maxPlayers; }
}
