package network;

public enum PacketType {
	PING       ('P'), // P(char)|id(int)|hash(String)
	MSG        ('M'), // M(char)|id(int)|msg(String),  Server message is chat with sender = 0
	CLIENT_INFO('C'), // C(char)|from(int)|msg(String)
	SERVER_INFO('S'), // S(char)|version(int)|client_id(int)|max_players(int)|min_players(int)
	READY	   ('R'),
	/* READY:
	 * Clientin lähettämänä tieto pelaajan valitsemasta hahmosta. Jos character === "", pelaaja perunut "valmiutensa"
	 *     R(char)|player_id(int)|character_id(String)			| Pelaaja valmis 
	 *     R(char)|player_id(int)|0(String)						| Pelaaja peruu valmiutensa

	 * Serverin lähettämänä tieto pelaajan roolista. Muiden hahmojen tiedot lähetetään vastaavina kun ilmoittavat olevansa valmiit.
	 *     R(char)|player_id(int)|role_id(String)				| Kun peli alkaa
	 *     R(char)|target_player_id(int)|character_id(String)	| Toinen pelaaja "valmis"
	 *     R(char)|target_player_id(int)|0(String)				| Toinen pelaaja perui "valmiutensa"
	 */
	ERROR    ('?');

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
			case '?': default: return ERROR;
		}
	}
}
