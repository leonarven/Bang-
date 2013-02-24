package network;

import java.nio.ByteBuffer;

public class Packet {
	protected final ByteBuffer buffer;

	// Warning: Two different constructors work very differently! (bad design :D)
	
	// TODO: This class should also handle: 
	// * endianness
	// * encodings

	// Don't copy ByteBuffer ctor
	public Packet( PacketType type, ByteBuffer buffer ) {
		buffer.putChar( 0, type.toChar() );
		this.buffer = buffer;
	}
	
	/* Moi Jaakko :)
	 * Kuitenkin oot ainoo joka lukee meidän koodia
	 * T. Sante & leonarven
	 */
	
	// Construct packet by copying the ByteBuffer.
	public Packet( ByteBuffer buffer, boolean copy ) {
		if ( copy ) {
			buffer.rewind();

			// Copy ByteBuffer
			byte[] array = new byte[buffer.remaining()];
			buffer.get( array );

			this.buffer = ByteBuffer.wrap( array );
			
			// Does ByteBuffer.wrap( byte[] ) ensure ByteBuffer.hasArray == true?
			assert this.buffer.hasArray();
		} else this.buffer = buffer;
	}

	public Packet( ByteBuffer buffer )
		{ this(buffer, true); }

	public ByteBuffer toByteBuffer() {
		buffer.rewind();
		return buffer;
	}
	
	public PacketType getType() {
		return PacketType.fromChar(this.buffer.getChar(0));
	}
}
