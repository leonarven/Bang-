package game;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

public class Engine {
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();
	
	// encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
	
	public static String DecodeString(ByteBuffer buffer) throws CharacterCodingException {
		decoder.reset();
		
		return decoder.decode(buffer).toString();
		
	}

	public static ByteBuffer EncodeString(String str) throws CharacterCodingException {
		encoder.reset();
		
		return encoder.encode(CharBuffer.wrap(str));
	}
	
}
