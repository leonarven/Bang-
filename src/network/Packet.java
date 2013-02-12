package network;

import java.nio.ByteBuffer;

public class Packet {
	private PacketType 	type;
	ByteBuffer 			message;

	// TODO: This class should also handle: 
	// * endianness
	// * encodings
	
	// Construct packet from copying the received ByteBuffer.
	public Packet( ByteBuffer buffer ) {
		buffer.rewind();

		// Copy ByteBuffer
		byte[] array = new byte[buffer.remaining()];
		buffer.get( array );
		
		// Does ByteBuffer.wrap( byte[] ) ensure ByteBuffer.hasArray == true?
		message = ByteBuffer.wrap( array );
		type = PacketType.fromChar( message.getChar() );

		// No need to have to and from field in every packet
	}

	public ByteBuffer toByteBuffer() {
		message.rewind();
		return message;
	}
	
	public ByteBuffer getMessage() { 
		message.position( Character.SIZE );
		return message.slice();
	}
	
	public PacketType getType() 
		{ return type; }
}
