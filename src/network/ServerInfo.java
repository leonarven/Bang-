package network;

import game.Engine;

import java.nio.ByteBuffer;

public class ServerInfo extends Packet {
	static String 		MOTD = "Hello client!";
	
	public ServerInfo(int version, int to) {
		super(PacketType.SERVER_INFO, 0, to, ByteBuffer.allocate(4 + MOTD.getBytes(Engine.charset).length));
		data.putInt(version);
		data.put(Engine.EncodeString(MOTD));
	}
	
	public static void setMOTD(String message) {
		MOTD = message;
	}
	
	public String getMOTD() {
		return getString(4);
	}
	
	public int getVersion() {
		return getInt(0);
	}
}
