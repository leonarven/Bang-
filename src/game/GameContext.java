package game;

import java.util.TreeMap;

public class GameContext {
	private static int playerCount = 0;
	
	private static TreeMap<Integer, Player> players = new TreeMap<Integer, Player>();
	
	public static Player GetPlayer(int id) {
		return players.get(id);
	}
	
	public static Player AddPlayer() {
		Player player = new Player(++playerCount);
		players.put(playerCount, player);
		return player;
	}
	
	public static int GetPlayerCount() {
		return playerCount;
	}
	
	public static void Reset() {
		playerCount = 0;
		
	}
}
