package game;

import java.util.HashMap;

import server.Connection;

public class Player {
	private final int id;			// Pelaaja tietoiseksi id:staan
	private final String nickname;
	private final String characterName;
	private boolean dead;			// Kuollut vai ei -> läpikäydäänkö pelaaja
	private PlayerType type;		// Pelaajahahmon tyyppi
	private Character character;	// Pelaajahahmo
	
	// Ensisijaiset ("tärkeät") tiedot
	private int range;				// Etäisyys, jolle voi ampua (päivittyy asekorttivaihdosten yhteydessä)
	private int health;				// "Elkut"
	
	// Toissijaiset tiedot
	private int distanceTo;		// --/
	private int distanceFrom;	// Pelaajan etäisyyksien ero "normaaliin"
	public void onTurn() {};
	public void onBang() {};
	
	public Player(int id, String nickname, Character character) {
		this.id			= id;
		this.nickname 	= nickname;
		this.range		= 1;
		this.dead		= false;
		this.character	= character;
		if (character != null) {
			this.health		= character.GetInitialHealth();
			// Sääntöjen mukainen lisäelämä seriffille
			if (type == SHERIFF) this.health+=1;

			this.onTurn			= character.onTurn();
			this.onBang			= character.onBang();
			
			this.characterName	= character.GetName();

			this.distanceTo		= character.GetInitialDistanceTo();
			this.distanceFrom	= character.GetInitialDistanceFrom();
		}
	}
	public Player(int id, String nickname) 
		{ this(id, "Unknown", null); }
	public Player(int id)
		{ this(id, "Unknown", null); }
	
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