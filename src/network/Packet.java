package network;

import java.nio.ByteBuffer;

public class Packet {
	protected final ByteBuffer buffer;

	// Warning: Two different constructors work very differently! (bad design :D)
	
	// TODO: This class should also handle: 
	// * endianness
	// * encodings
	
	/* Moi Jaakko :)
	 * Kuitenkin oot ainoo joka lukee meidän koodia
	 * T. Sante & leonarven
	 */
	
	// Construct packet by copying the ByteBuffer.
	public Packet( ByteBuffer buffer, boolean copy ) {

//		System.out.println("PACKET >> :");
//		System.out.println("1 - lim:"+buffer.limit());
//		System.out.println("1 - pos:"+buffer.position());
//		System.out.println("1 - rem:"+buffer.remaining());

		int length = 0; // buffer.position();
		char ch;

		// Ei uskalleta olettaa, että position on aina lopussa
		buffer.position(0);
		while(buffer.remaining() > 0 && !java.lang.Character.isWhitespace(ch = (char)buffer.get()))
			length++;

//		System.out.println("2 - lim:"+buffer.limit());
//		System.out.println("2 - pos:"+buffer.position());
//		System.out.println("2 - rem:"+buffer.remaining());
//		System.out.println("2 - len:"+length);

		if ( copy ) {
			buffer.limit(buffer.position());
			buffer.position(0);
			
			// Copy ByteBuffer
			byte[] array = new byte[length];
			buffer.get(array);

			ByteBuffer _buffer = ByteBuffer.allocate(length);

			_buffer = ByteBuffer.wrap( array );
			
			// TODO: ota vain length-palanen _buffer:sta
			this.buffer = _buffer;
			
			// Does ByteBuffer.wrap( byte[] ) ensure ByteBuffer.hasArray == true?
			assert this.buffer.hasArray();
		} else {

			// TODO: ota vain length-palanen buffer:sta
			this.buffer = buffer;
		}
		
		

//		while(_buffer.remaining() > 0 && !java.lang.Character.isWhitespace(ch = (char)_buffer.get()))
//			lenToAllocate++;

/*

		ByteBuffer _buffer = ;
		char ch;
		while(buffer.remaining() > 0 && !java.lang.Character.isWhitespace(ch = (char)_buffer.get()))
		    this.buffer.putChar(ch);
*/
		
//		System.out.println("3 - lim:"+this.buffer.limit());
//		System.out.println("3 - pos:"+this.buffer.position());
//		System.out.println("3 - rem:"+this.buffer.remaining());
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
