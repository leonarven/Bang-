package network;

import java.nio.ByteBuffer;
import game.JSONObject;

//Packet format:
//0: 	char 	= PacketType
//2 	int 	= client id
//6 	string	= json

public class ServerInfo {
	// FIXME: Miksi versiotieto paketissa?
	public static final int VERSION = 1;
	
	private int id;
	private JSONObject json;
	
	public ServerInfo( JSONObject json ) {
		this.json = json;
	}
	public ServerInfo( Object json ) {
		this.json = new JSONObject(json);
	}
	public ServerInfo( String json ) {
		this.json = new JSONObject(json);
	}
	
	public ServerInfo( Packet packet ) {		
		ByteBuffer buffer = packet.toByteBuffer();
		
		if ( buffer.limit() <= 6 || PacketType.fromChar(buffer.getChar(0)) != PacketType.SERVER_INFO ) {
			// TODOThrowSomething();
		}
		
		buffer.position( 2 );
		this.id 	= buffer.getInt();
		this.json	= new JSONObject(new String( buffer.array(), 6, buffer.limit() - 6 ));
	}
	
	public Packet toPacket() {
		// FIXME: Miksi lähtee 144 tavua?
		byte[] bytes = json.toString().getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE + Integer.SIZE + bytes.length ); // FIXME depends on encoding?
		buffer.putChar( PacketType.SERVER_INFO.toChar() );
		buffer.putInt( id );
		buffer.put( bytes ); // FIXME encodings.
		return new Packet( PacketType.SERVER_INFO, buffer );
	}
	
	public int getId()
		{ return this.id; }
	
	public JSONObject getJson()	
		{ return this.json; }
}
