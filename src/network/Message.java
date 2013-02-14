package network;

import java.nio.ByteBuffer;

//Packet format:
//0: 	char 	= PacketType
//2: 	int 	= from 
//4... 	String 	= message

public class Message {

	protected final PacketType type;
	protected final int 		id;
	protected final String	message;
	
	public Message( int id, String message ) {		
		this.type 		= PacketType.MSG;
		this.id 		= id;
		this.message 	= message;
	}

	public Message( PacketType type, int id, String message ) {		
		this.type 		= type;
		this.id 		= id;
		this.message 	= message;
	}
	
	public Message( Packet packet ) {
		ByteBuffer buffer = packet.toByteBuffer();
		
		assert buffer.limit() > 4;
		
		this.type = PacketType.MSG;
		this.id = buffer.getInt( 2 );
		this.message = new String( buffer.array(), 6, buffer.limit() - 6 );
	}

	public String getMessage() 
		{ return message; }
	
	public int getSenderId() 
		{ return id; }
	
	// Other packets might need to override this?
	public Packet toPacket() {
		
		byte[] bytes = message.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE + Integer.SIZE + bytes.length ); // FIXME depends on encoding?
		buffer.putChar( type.toChar() );
		buffer.putInt( id );
		buffer.put( bytes ); // FIXME encodings.
		
		// Don't make copy of buffer
		return new Packet( type, buffer );	
	}
}
