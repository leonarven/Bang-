package network;

import java.nio.ByteBuffer;

public class ServerInfo extends Packet {
	static final int 	VERSION = 0x1;
	static String 		MOTD = "Hello client!";
	
	public ServerInfo(ByteBuffer buffer) {
		super(buffer);
	}
	
	public ServerInfo(int to) {
		super(PacketType.SERVER_INFO, 0, to, ByteBuffer.allocate(4 + MOTD.length() * 2).putInt(VERSION).put(MOTD.getBytes()));
	}
	
	public static void setMOTD(String message) {
		MOTD = message;
	}
	
	public String getMOTD() {
		data.position(4);
		return new String(data.array());
	}
	
	public int getVersion() {
		data.position(0);
		return data.getInt();
	}
}
