package game;

public class Player {
	private final int id;			// Pelaaja tietoiseksi id:staan
	
	private PlayerType type;		// Pelaajahahmon tyyppi
	private Character character;	// Pelaajahahmo
	private int range;				// Etäisyys, jolle voi ampua (päivittyy asekorttivaihdosten yhteydessä)
	private int health;				// "Elkut"

	
	public Player( int id ) {
		this.id	= id;
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