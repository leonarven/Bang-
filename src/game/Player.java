package game;

public class Player {
	private final int id;			// Pelaaja tietoiseksi id:staan
	private boolean dead;			// Kuollut vai ei -> läpikäydäänkö pelaaja
	private PlayerType type;		// Pelaajahahmon tyyppi
	private Character character;	// Pelaajahahmo
	private int range;				// Etäisyys, jolle voi ampua (päivittyy asekorttivaihdosten yhteydessä)
	private int health;				// "Elkut"
	
	public Player(int id, PlayerType type, Character character) {
		this.id			= id;
		this.type		= type;
		this.character	= character;
		this.range		= 2;	// TODO: mikä on oletusetäisyys?
		this.dead		= false;
		if (character != null) {
			this.health		= character.GetInitialHealth();
		}
	}
	public Player(int id, PlayerType type)
		{ this(id, type, null); }
	public Player(int id)
		{ this(id, null, null); }
	
	public int			GetId()			{ return this.id; }
	public boolean		GetDead()		{ return this.dead; }
	public PlayerType	GetType()		{ return this.type; }
	public Character	GetCharacter()	{ return this.character; }
	public int			GetRange()		{ return this.range; }
	public int			GetHealth()		{ return this.health; }
	public boolean		IsDead()		{ return this.GetDead(); } //Kuulostaa kivemmalta

	public void SetRange(int range)		{ this.range = range; }
	public void SetHealth(int health)	{ this.health = health; }
}