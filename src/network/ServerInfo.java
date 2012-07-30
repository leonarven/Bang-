package network;

import java.nio.ByteBuffer;

public class ServerInfo extends Packet {
	static final int 	VERSION = 0x1;
	static ByteBuffer data = ByteBuffer.allocate(4).putInt(VERSION);
	
	public ServerInfo(ByteBuffer buffer) {
		super(buffer);
	}
	
	public ServerInfo(int to) {
		super(PacketType.SERVER_INFO, 0, to, data.array());
	}
	
	public static void setMOTD(String message) {
		if (message.length() * 2 > 1024 - 4) {
			return; // Too long
		}
		
		data = ByteBuffer.allocate(4 + message.length() * 2).putInt(VERSION).put(message.getBytes());
	}
}
