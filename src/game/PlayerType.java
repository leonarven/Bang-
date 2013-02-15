package game;

import network.PacketType;

public enum PlayerType {
	SHERIFF('S'),
	DEPUTY('D'),
	OUTLAW('O'),
	RENEGADE('R'),
	UNKNOWN('?');
	
	private final char c;
	
	private PlayerType(char c) { this.c = c; }

	public char toChar()     { return this.c; }
	public String toString() { return this.c+""; }

	public static PlayerType fromChar(char c) {
		switch(c) {
			case 'S': return SHERIFF;
			case 'D': return DEPUTY;
			case 'O': return OUTLAW;
			case 'R': return RENEGADE;
			case '?': default: return UNKNOWN;
		}
	}
	public static PlayerType fromString(String s)
		{ return fromChar(s.charAt(0)); }
}
