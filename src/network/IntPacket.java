package network;

import java.nio.ByteBuffer;

public class IntPacket {
	private final PacketType type;
	private final int id;
	private final int data;
	
	IntPacket(PacketType type, int id, int data) {
		this.type = type;
		this.id = id;
		this.data = data;
	}
	
	IntPacket(Packet packet) throws Exception {
		ByteBuffer buffer = packet.toByteBuffer();
		if (buffer.limit() != 10) {
			throw new Exception("Invalid packet length");
		}
		
		this.type 	= PacketType.fromChar(buffer.getChar(0));
		this.id 	= buffer.getInt(2);
		this.data 	= buffer.getInt(6);
	}
	
	public PacketType getType() 
		{ return type; }
	
	public int getId() 
		{ return id; }
	
	public int getData() 
		{ return data; }
	
	public Packet toPacket() {
		ByteBuffer buffer = ByteBuffer.allocate(Character.SIZE + Integer.SIZE);
		buffer.putChar(type.toChar());
		buffer.putInt(data);
		
		return new Packet(type, buffer);
	}
}
