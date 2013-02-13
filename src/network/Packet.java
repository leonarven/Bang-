package network;

import java.nio.ByteBuffer;

public class Packet {
	protected final PacketType type;
	protected final ByteBuffer buffer;

	// Warning: Two different constructors work very differently! (bad design :D)
	
	// TODO: This class should also handle: 
	// * endianness
	// * encodings
	
	public static Packet illegalPacket() {
		return new Packet( PacketType.ILLEGAL, ByteBuffer.allocate( Character.SIZE ).putChar( PacketType.ILLEGAL.toChar() ));
	}

	// Don't copy ByteBuffer ctor
	public Packet( PacketType type, ByteBuffer buffer ) {
		buffer.putChar( 0, type.toChar() );
		this.type = type;
		this.buffer = buffer;
	}
	
	// Construct packet by copying the ByteBuffer.
	public Packet( ByteBuffer buffer ) {
		buffer.rewind();

		// Copy ByteBuffer
		byte[] array = new byte[buffer.remaining()];
		buffer.get( array );

		this.buffer = ByteBuffer.wrap( array );
		type = PacketType.fromChar( buffer.getChar() );

		// Does ByteBuffer.wrap( byte[] ) ensure ByteBuffer.hasArray == true?
		assert this.buffer.hasArray();
	}

	public ByteBuffer toByteBuffer() {
		buffer.rewind();
		return buffer;
	}
	
	public PacketType getType() 
		{ return type; }
	
	public void setType( PacketType type ) 
		{ buffer.putChar( type.toChar() ); }
}
