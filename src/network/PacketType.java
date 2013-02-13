package network;

public enum PacketType {
	PING       ('P'),
	MSG        ('M'), // Server message is chat with sender = 0
	CLIENT_INFO('C'),
	SERVER_INFO('S'),
	READY	   ('R'),
	ILLEGAL    ('?');

	private final char c;
	
	private PacketType(char c) {
		this.c = c;
	}

	public char toChar() {
		return this.c;
	}

	public static PacketType fromChar(char c) {
		switch(c) {
			case 'P': return PING;
			case 'C': return CLIENT_INFO;
			case 'S': return SERVER_INFO;
			case 'M': return MSG;
			case 'R': return READY;
			case '?': default: return ILLEGAL;
		}
	}
}
