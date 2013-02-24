package network;

import java.nio.ByteBuffer;

public class Error {
	private final int errorCode;
	
	public Error( int errorCode ) {		
		this.errorCode = errorCode;
	}
	
	public Error( Packet packet ) {
		ByteBuffer buffer = packet.toByteBuffer();
		
		if (buffer.limit() != 6 || buffer.getChar(0) != PacketType.ERROR.toChar() ) {
			// TODO throw something
		}
		
		this.errorCode = buffer.getInt( 2 );
	}
		
	public ErrorCode getErrorCode() {
		return ErrorCode.fromInt( errorCode );
	}
	
	// Other packets might need to override this?
	public Packet toPacket() {
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE + Integer.SIZE );
		buffer.putChar( PacketType.ERROR.toChar() );
		buffer.putInt( errorCode );
		
		return new Packet( PacketType.ERROR, buffer );	
	}
}
