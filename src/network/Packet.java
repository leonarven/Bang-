package network;

import java.nio.ByteBuffer;

public class Packet {
	private PacketType type;
	private int        from;
	private int        to;
	private byte[]     data;

	public Packet(ByteBuffer buffer) {
		buffer.position(0);
		this.type = PacketType.fromChar(buffer.getChar(0));
		this.from = buffer.getInt(2);
		this.to   = buffer.getInt(6);

		buffer.position(10);
		this.data = new byte[buffer.remaining()];
		buffer.get(data, 0, data.length);
		buffer.position(0);
	} 
	
	public Packet(PacketType type, int from, int to, byte[] data) {
		this.type = type;
		this.from = from;
		this.to   = to;
		this.data = data;
	}
	public Packet(PacketType type, int from, int to, String data) 
		{ this(type, from, to, data.getBytes()); }
	public Packet(char type, int from, int to, byte[] data) 
		{ this(PacketType.fromChar(type), from, to, data); }
	public Packet(char type, int from, int to, String data) 
		{ this(type, from, to, data.getBytes()); }

	public ByteBuffer toByteBuffer() {
		ByteBuffer tmp = ByteBuffer.allocate(10+this.data.length);
		tmp.putChar(this.type.toChar());
		tmp.putInt(this.from);
		tmp.putInt(this.to);
		tmp.put(this.data);
		return tmp;
	}
	
	public int getFrom()
		{ return this.from; }
	public int getTo()
		{ return this.to; }
	public byte[] getData()
		{ return this.data; }
	public PacketType getType()
		{ return this.type; }

	public void setFrom(int from)
		{ this.from = from; }
	public void setTo(int to)
		{ this.to = to; }
	public void setData(byte[] data)
		{ this.data = data; }
	public void setType(PacketType type)
		{ this.type = type; }
	public void setType(char type)
		{ this.type = PacketType.fromChar(type); }

	
	@Override
	public String toString() {
		return new String(this.data);
	}
}
