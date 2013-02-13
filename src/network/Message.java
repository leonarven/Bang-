package network;

import java.nio.ByteBuffer;

public class Message {

	protected final int 		id;
	protected final String	message;
	
	public Message( int id, String message ) {		
		this.id 		= id;
		this.message 	= message;
	}

	public Message( Packet packet ) {
		ByteBuffer buffer = packet.toByteBuffer();
		
		this.id = buffer.getInt( 2 );
		this.message = new String( buffer.array(), 6, buffer.limit() - 6 );
	}

	public String getMessage() 
		{ return message; }
	
	public int getSenderId() 
		{ return id; }
	
	// Other packets might need to override this?
	public Packet getPacket() {
		
		byte[] bytes = message.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE + Integer.SIZE + bytes.length ); // FIXME depends on encoding?
		buffer.putChar( PacketType.MSG.toChar() );
		buffer.putInt( id );
		buffer.put( bytes ); // FIXME encodings.
		
		// Don't make copy of buffer
		return new Packet( PacketType.MSG, buffer );
		
	}
}
