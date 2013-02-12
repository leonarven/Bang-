package game;

import java.util.TreeMap;

//This class should only have datastructures and functions that are usefull to both client and server.
public class Card {
	private CardType type;
	private TreeMap<Integer, Integer> data = new TreeMap<Integer, Integer>(); // Esim. aseen kantama
	
	public Card(CardType type, String name, TreeMap<Integer, Integer> data) {
		this.type = type;
		this.data = data;
	}
	public Card( CardType type, String name )
		{ this(type, name,  new TreeMap<Integer, Integer>()); }
	public Card( CardType type )
		{ this(type, "", new TreeMap<Integer, Integer>()); }

	// GET values
	public TreeMap<Integer, Integer>	GetData()				{ return this.data; }
	public Integer						GetData( Integer key )	{ return this.data.get(key); }
	public CardType						GetType()				{ return this.type; }

	//SET values
	public void SetData( Integer key, Integer value ) // Jos kortin ominaisuudet muuttuvat pelin myötä
		{ this.data.put(key, value); }
}
