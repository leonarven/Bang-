package game;

import java.util.Collection;
import java.util.HashMap;

public class GameContext {
	
	private static HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	
	public static void Reset() {
		players.clear();
	}

	public static Player AddPlayer(int id, String name) {
		Player player = new Player(id, name);
		players.put(id, player);
		return player;
	}

	public static Collection<Player> getPlayers() { return players.values(); }
	public static Player	GetPlayer(int id)	{ return players.get(id); }
	public static int		GetPlayerCount()	{ return players.size(); }
}
