package network;

import java.nio.ByteBuffer;

abstract public class PacketBase {
	private final PacketType type;
	private final int id;
	
	protected PacketBase( PacketType type, int id ) {
		this.type = type;
		this.id = id;
	}
	
	protected PacketBase( Packet packet ) throws Exception {

		ByteBuffer buffer = packet.toByteBuffer();
		if (buffer.limit() < 6) {
			throw new Exception("Invalid packet length");
		}
		
		this.type 	= PacketType.fromChar(buffer.getChar(0));
		this.id 	= buffer.getInt(2);

		buffer.position(6);

		this.setData(buffer);
	}
	
	public PacketType getType() 
		{ return type; }
	
	public int getId() 
		{ return id; }
	
	
	abstract public Packet toPacket();

	abstract protected void setData(ByteBuffer data);
}
