package game;

public class Character { // Prototyyppi pelaajahahmosta pelaajalle Player
	private String name;
	private int initialHealth;
	
	public Character(String name, int initialHealth) {
		this.name = name;
		this.initialHealth = initialHealth;
	}
	
	public String	GetName()			{ return this.name; }
	public int 		GetInitialHealth()	{ return this.initialHealth; }
}
