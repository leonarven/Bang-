package network;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Packet {
	public static Charset charset = Charset.forName("UTF-8");
	private static CharsetEncoder encoder = charset.newEncoder();
	private static CharsetDecoder decoder = charset.newDecoder();
	private static final Object encoderLock = new Object();
	private static final Object decoderLock = new Object();
	
	private PacketType 	type;
	private String 		message;
	
	public Packet(ByteBuffer buffer) {
		buffer.position(0);
		this.type = PacketType.fromChar(buffer.getChar(0));
		
		// Would it be better to have multiple instances of encoder/decoder?
		synchronized( decoderLock ) {
			try {	
				this.message = decoder.decode( buffer ).toString();
			} catch (CharacterCodingException e) {
				System.err.println( "Failed to decode packet: " + e.getMessage() );
			}
		}
	}
	
	public ByteBuffer toByteBuffer() {
		synchronized( encoderLock ) {
			try {
				// Only send utf-8 encoded data...?
				return encoder.encode( CharBuffer.wrap( type.toChar() + message ));
			} catch (CharacterCodingException e) {
				System.err.println( "Failed to encode packet: " + e.getMessage() );
				return null;
			}
		}
	}
}
