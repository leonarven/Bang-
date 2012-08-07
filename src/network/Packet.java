package network;

import java.nio.ByteBuffer;

public class Packet {
	public PacketType 	type;
	public int        	from;
	public int        	to;
	public ByteBuffer 	data;
	public Packet(ByteBuffer buffer) {
		buffer.position(0);
		this.type = PacketType.fromChar(buffer.getChar(0));
		this.from = buffer.getInt(2);
		this.to = buffer.getInt(6);
		
		buffer.position(10);
		data = buffer.slice();
		
		buffer.position(0);
	}
	
	public Packet(PacketType type, int from, int to, ByteBuffer data) {
		this.type = type;
		this.from = from;
		this.to   = to;

		data.position(0);
		this.data = data;
	}
	public Packet(PacketType type, int from, int to, byte[] data)
		{ this(type, from, to, ByteBuffer.wrap(data)); }
	public Packet(PacketType type, int from, int to, String data)
		{ this(type, from, to, data.getBytes()); }

	public Packet(char type, int from, int to, ByteBuffer data)
		{ this(PacketType.fromChar(type), from, to, data); }
	public Packet(char type, int from, int to, byte[] data)
		{ this(PacketType.fromChar(type), from, to, ByteBuffer.wrap(data)); }
	public Packet(char type, int from, int to, String data)
		{ this(type, from, to, data.getBytes()); }
	
	public ByteBuffer toByteBuffer() {
		ByteBuffer tmp = ByteBuffer.allocate(10+this.data.limit());
		tmp.putChar(this.type.toChar());
		tmp.putInt(this.from);
		tmp.putInt(this.to);
		
		this.data.position(0);
		tmp.put(this.data.array());
		tmp.position(0);
		return tmp;
	}
}
