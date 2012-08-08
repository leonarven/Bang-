package network;

import game.Engine;

import java.nio.ByteBuffer;

public class Packet {
	private PacketType 	type;
	private int        	from;
	private int        	to;
	protected ByteBuffer data;
	
	public Packet(ByteBuffer buffer) {
		buffer.position(0);
		this.type = PacketType.fromChar(buffer.getChar(0));
		this.from = buffer.getInt(2);
		this.to = buffer.getInt(6);
		
		buffer.position(10);
		data = buffer.slice();
		
		buffer.position(0);
	}
	
	protected Packet() {}
	
	public Packet(PacketType type, int from, int to, ByteBuffer data) {
		this.type = type;
		this.from = from;
		this.to   = to;

		data.position(0);
		this.data = data;
	}

	public Packet(char type, int from, int to, ByteBuffer data)
		{ this(PacketType.fromChar(type), from, to, data); }

	public Packet(PacketType type, int from, int to, String data)
		{ this(type, from, to, Engine.EncodeString(data)); }
	
	public Packet(char type, int from, int to, String data)
		{ this(PacketType.fromChar(type), from, to, data); }
	
	public PacketType getType()
		{ return type; }
	public int getFrom()
		{ return this.from; }
	public int getTo()
		{ return this.to; }
	public ByteBuffer getData()
		{ return this.data; }
	
	public String getString(int index) { 
		data.position(index);
		return Engine.DecodeString(data); 
	}
	
	public int getInt(int index)
		{ return data.getInt(index); }
	
	public long getLong(int index)
		{ return data.getLong(index); }
	
	public ByteBuffer toByteBuffer() {
		ByteBuffer tmp = ByteBuffer.allocate(10+this.data.limit());
		// Header
		tmp.putChar(this.type.toChar());
		tmp.putInt(this.from);
		tmp.putInt(this.to);
		// Data
		this.data.position(0);
		tmp.put(data);
		tmp.position(0);
		
		return tmp;
	}
}
