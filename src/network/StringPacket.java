package network;

import java.nio.ByteBuffer;

public class StringPacket {
	protected final PacketType type;
	protected final int 		id;
	protected final String	data;

	public StringPacket(PacketType type, int id, String message) {		
		this.type 		= type;
		this.id 		= id;
		this.data 	= message;
	}
	
	public StringPacket(Packet packet) throws Exception {
		ByteBuffer buffer = packet.toByteBuffer();
		
		if (buffer.limit() < 4) {
			throw new Exception("Invalid packet length");
		}

		this.type 		= PacketType.MSG;
		this.id 		= buffer.getInt( 2 );
		this.data 	= new String( buffer.array(), 6, buffer.limit() - 6 );
	}

	public PacketType getType() 
		{ return type; }
	
	public int getId() 
		{ return id; }

	public String getData() 
		{ return data; }
	
	// Other packets might need to override this?
	public Packet toPacket() {
		
		byte[] bytes = data.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE + Integer.SIZE + bytes.length ); // FIXME depends on encoding?
		buffer.putChar( type.toChar() );
		buffer.putInt( id );
		buffer.put( bytes ); // FIXME encodings.
		
		// Don't make copy of buffer
		return new Packet( type, buffer );	
	}
}