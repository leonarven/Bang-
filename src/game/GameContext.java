package game;

import java.util.*;

import game.Player;

// This class should only have datastructures and functions that are usefull to both client and server.
public class GameContext {
	private boolean isRunning = false;
	
	private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	private LinkedList<Card> discardPile = new LinkedList<Card>();
	
	public GameContext() {

	
	}
	
	public void reset() {
		players.clear();
		discardPile.clear();
	}
	
	public void addPlayer( Player player ) {
		if ( !isRunning ) {
			players.put( player.getId(), player );
		}
	}
	
	Player getPlayer( int id ) 
		{ return players.get( id ); }
	
	public boolean hasPlayer( int id )
		{ return players.containsKey( id ); }
	
	public int getPlayerCount() 
		{ return players.size(); }
	
	public boolean isRunning() 
		{ return this.isRunning; }
	
	void start() 
		{ this.isRunning = true; }

}
