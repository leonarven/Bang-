package network;

public enum PacketType {
	PING       ('P'),
	MSG        ('M'), // Server message is chat with sender = 0
	CLIENT_INFO('C'),
	SERVER_INFO('S'),
	ILLEGAL    ('?');

	private PacketType(char c) {
		this.c = c;
	}
	
	private final char c;

	public char toChar() {
		return this.c;
	}
	
	public static PacketType fromChar(char c) {
		switch(c) {
			case 'P': return PING;
			case 'C': return CLIENT_INFO;
			case 'S': return SERVER_INFO;
			case 'M': return MSG;
			case '?': default: return ILLEGAL;
		}
	}
}
