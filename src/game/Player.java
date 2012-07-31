package game;

public class Player {
	private final int id;			// Pelaaja tietoiseksi id:staan
	private final String nickname;
	private boolean dead;			// Kuollut vai ei -> läpikäydäänkö pelaaja
	private PlayerType type;		// Pelaajahahmon tyyppi
	private Character character;	// Pelaajahahmo
	private int range;				// Etäisyys, jolle voi ampua (päivittyy asekorttivaihdosten yhteydessä)
	private int health;				// "Elkut"
	
	public Player(int id, String nickname) {
		this.id			= id;
		this.nickname 	= nickname;
		this.range		= 1;
		this.dead		= false;
		if (character != null) {
			this.health		= character.GetInitialHealth();
		}
	}
	public Player(int id)
		{ this(id, "Unknown"); }
	
	public String 		getName()		{ return this.nickname; }
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