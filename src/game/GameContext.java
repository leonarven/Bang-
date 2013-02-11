package game;

import java.util.*;

import game.Player;


public class GameContext {
	private boolean isRunning = false;
	
	private HashMap<Integer, Player> players;
	
	public GameContext() {
		players = new HashMap<Integer, Player>();
	
	}
	
	public void addPlayer( Player player ) {
		if ( !isRunning ) {
			players.put( player.getId(), player );
		}
	}
	
	Player getPlayer( int id ) 
		{ return players.get( id ); }
	
	public int getPlayerCount() 
		{ return players.size(); }
	
	public boolean isRunning() 
		{ return this.isRunning; }
	
	void start() 
		{ this.isRunning = true; }
}
