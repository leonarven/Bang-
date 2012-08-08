package game;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

public class Engine {
	public static Charset charset = Charset.forName("UTF-8");
	
	public static String DecodeString(ByteBuffer buffer) {
		try {
			return charset.newDecoder().decode(buffer).toString();
		} catch (CharacterCodingException e) {
			System.err.println("Failed to decode " + e);
		}
		
		return null;
	}

	public static ByteBuffer EncodeString(String str) {
		try {
			return charset.newEncoder().encode(CharBuffer.wrap(str));
		} catch (CharacterCodingException e) {
			System.err.println("Failed to encode " + e);
		}
		
		return null;
	}
	
}
