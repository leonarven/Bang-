package network;

import game.Engine;

import java.nio.ByteBuffer;

public class ClientInfo extends Packet {
	public ClientInfo(int id, String name) {
		super(PacketType.CLIENT_INFO, 0, 0, ByteBuffer.allocate(4 + name.getBytes(Engine.charset).length));
		data.putInt(id);
		data.put(Engine.EncodeString(name));
	}
	
	public ClientInfo(game.Player player)
		{ this(player.GetId(), player.getName()); }
	
	public ClientInfo(int id) 
		{ this(id, "Unknown_"+id); }

	public ClientInfo(Packet packet) 
		{ super(packet.getType(), packet.getFrom(), packet.getTo(), packet.data); }

	public String getNickname() {
		return getString(4);
	}
	
	public int getId() {
		return getInt(0);
	}
}
