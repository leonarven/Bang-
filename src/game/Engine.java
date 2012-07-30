package game;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

public class Engine {
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();
	
	// encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
	
	public static String DecodeString(ByteBuffer buffer) {
		decoder.reset();
		
		try {
			return decoder.decode(buffer).toString();
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static ByteBuffer EncodeString(String str) {
		encoder.reset();
		
		try {
			ByteBuffer result = encoder.encode(CharBuffer.wrap(str));
			return result;
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
