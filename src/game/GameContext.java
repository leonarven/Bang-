package game;

import java.util.*;


public class GameContext {
	
	private static HashMap<Integer, Player>    players    = new HashMap<Integer, Player>();
	private static HashMap<Integer, Card>      cards      = new HashMap<Integer, Card>();
	private static HashMap<Integer, Character> characters = new HashMap<Integer, Character>();

	public GameContext() {
		JSONContentReader r = new JSONContentReader("content/initialCards.json");
		System.out.println(r.getValueById("13"));
		System.err.println(r);
	} 
	
	public static void Reset() {
		players.clear();
	}
	
	public static void AddPlayer(Player player) 
		{ players.put(player.GetId(), player); }
	
	public static Player AddPlayer(int id, String name) {
		Player player = new Player(id, name);
		AddPlayer(player);
		return player;
	}
	
	public static void RemovePlayer(int id) {
		players.remove(id);
	}

	public static Collection<Player> getPlayers() { return players.values(); }
	public static Player	getPlayer(int id)	{ return players.get(id); }
	public static int		getPlayerCount()	{ return players.size(); }
}
