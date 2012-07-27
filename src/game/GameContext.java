package game;

import java.util.TreeMap;

public class GameContext {
	private int playerCount = 0;

	private TreeMap<Integer, Player> players = new TreeMap<Integer, Player>();
	
	public void Reset() {
		this.playerCount = 0;
		this.players.clear();
	}

	public Player AddPlayer() {
		Player player = new Player(++this.playerCount);
		this.players.put(this.playerCount, player);
		return player;
	}

	public Player	GetPlayer(int id)	{ return this.players.get(id); }
	public int		GetPlayerCount()	{ return this.playerCount; }
}
