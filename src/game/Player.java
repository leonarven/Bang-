package game;

import java.util.LinkedList;

//This class should only have datastructures and functions that are usefull to both client and server.
public class Player {
	private final int 	id;					// Pelaaja tietoiseksi id:staan
	private String 		name;
	
	private PlayerType 	type;				// Pelaajahahmon tyyppi
	private Character 	character;			// Pelaajahahmo
	private int 		range;				// Etäisyys, jolle voi ampua (päivittyy asekorttivaihdosten yhteydessä)
	private int 		health;				// "Elkut"
	
	private LinkedList<Card> equipment = new LinkedList<Card>();

	
	public Player( int id, String name ) {
		this.id	= id;
		this.name = name;
	}
	
	public int			getId()			{ return this.id; }
	
	public PlayerType	getType()		{ return this.type; }
	public Character	getCharacter()	{ return this.character; }
	public int			getRange()		{ return this.range; }
	public int			getHealth()		{ return this.health; }
	public boolean		isDead()		{ return this.health == 0; }

	public void setRange(int range)		{ this.range = range; }
	public void setHealth(int health)	{ this.health = health; }
}