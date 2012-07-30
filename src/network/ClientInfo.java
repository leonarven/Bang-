package network;

import java.nio.ByteBuffer;

public class ClientInfo extends Packet {
	
	public ClientInfo(ByteBuffer buffer) {
		super(buffer);
	}
	
	public ClientInfo(int id, String nickname) {
		super(PacketType.CLIENT_INFO, 0, 0, ByteBuffer.allocate(4 + nickname.length() * 2).putInt(id).put(nickname.getBytes()));
	}
	
	public String getNickname() {
		data.position(4);
		return new String(data.array());
	}
	
	public int getId() {
		data.position(0);
		return data.getInt();
		
		
	}
	
	
}
