package game;

import java.util.LinkedList;

public class Player {
	private final int id;			// Pelaaja tietoiseksi id:staan
	private final String name;
	private String characterName;
	private boolean dead;			// Kuollut vai ei -> läpikäydäänkö pelaaja
	private PlayerType type;		// Pelaajahahmon tyyppi
	private Character character;	// Pelaajahahmo
	private boolean 	ready;				
	
	// Ensisijaiset ("tärkeät") tiedot
	private int range;				// Etäisyys, jolle voi ampua (päivittyy asekorttivaihdosten yhteydessä)
	private int health;				// "Elkut"
	
	private LinkedList<Card> equipment = new LinkedList<Card>();
	private LinkedList<Card> handCards = new LinkedList<Card>();
	
	// Toissijaiset tiedot
	private int distanceTo;		// --/
	private int distanceFrom;	// Pelaajan etäisyyksien ero "normaaliin"
	public void onTurn() {};
	public void onBang() {};
	
	public Player( int id, String name ) {
		this.id			= id;
		this.name 		= name;
		this.ready 		= false;
		
		// Mun versiossa pelaaja luokka tehdään jo ennen kun peli on alkanu :D
		// katso src/server/Game.java
		
	/*
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
		*/
	}
	
	public int			getId()			{ return this.id; }
	public String 		getName() 		{ return this.name; }
	
	public PlayerType	getType()		{ return this.type; }
	public Character	getCharacter()	{ return this.character; }
	public int			getRange()		{ return this.range; }
	public int			getHealth()		{ return this.health; }
	public boolean		isDead()		{ return this.health == 0; }

	public void setRange(int range)		{ this.range     = range; }
	public void setHealth(int health)	{ this.health    = health; }
	public void setType(PlayerType type){ this.type    = type; }
	public void setCharacter(Character character) {
		this.character = character;
		this.characterName = character.GetName();
	}
	
	public void 	setReady( boolean ready ) 	{ this.ready = ready; }
	public boolean 	isReady() 					{ return this.ready; }
}