package network;

public enum PacketType {
	PING       ('P'),
	CHAT       ('C'),
	MSG        ('M'),
	CLIENT_INFO('I'),
	SERVER_INFO('S'),
	GAME_STATUS('G'),
	READY      ('R'),
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
			case 'C': return CHAT;
			case 'M': return MSG;
			case '?': default: return ILLEGAL;
		}
	}
}
