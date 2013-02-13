package game;

public class Character { // Prototyyppi pelaajahahmosta pelaajalle Player
	private String name;
	private int initialHealth;

	private int initialDistanceTo = 0;   // Alkutäisyys Pelaajaan N.N...
	private int initialDistanceFrom = 0; // Alkuetäisyys Pelaajasta N.N...

	/* Mietin toteutukseen anonyymejä luokkia sekä jotain vaihtoehtoista
	 * lambda-funktiototeutusta, mutta ajattelin, että tämä malli tehokkain.
	 * Vakioarvot valmiina sekä vaihto voidaan asettaa Character:n luonnissa jos tarvetta.
	 */
	public void onTurn() {};
	public void onBang() {};
	public void onHandEmpty() {};
	public void onTakeDamage() {};
	
	public Character(String name, int initialHealth) {
		this.name = name;
		this.initialHealth = initialHealth;
	}
	
	public String	GetName()			{ return this.name; }
	public int 		GetInitialHealth()	{ return this.initialHealth; }
	public int 		GetInitialDistanceTo()		{ return this.initialDistanceTo; }
	public int 		GetInitialDistanceFrom()	{ return this.initialDistanceFrom; }
}
