package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import network.Packet;

// Content of this class should be synchronized with the server and clients
public abstract class Game {
	protected HashMap<Integer, Player> players 	= new HashMap<Integer, Player>();
	protected LinkedList<Card> discardPile 		= new LinkedList<Card>();
	protected ArrayList<Integer> playerOrder	= new ArrayList<Integer>();
	
	protected boolean isRunning = false;

	protected int maxPlayers = 8;
	protected int minPlayers = 1;
	protected int turnIterator = 0;
		
	public Game() {
		
	}
	
	public final void start() { 
		isRunning = true; 
		this.doStart();
	}
	public final void reset() {
		players.clear();
		playerOrder.clear();
		discardPile.clear();
		isRunning = false;
		this.doReset();
	}
	
	abstract public void handlePacket(Packet packet);
	abstract protected void preTurn();
	
	abstract protected void doStart();
	abstract protected void doReset();
	
	public int		getNthPlayer( int n ) { return playerOrder.get(n); }
	public boolean 	isRunning() 		{ return this.isRunning; }
	public int getMinPlayers()			{ return this.minPlayers; }
	public int getMaxPlayers() 			{ return this.maxPlayers; }
	public Player 	getPlayer( int id ) { return players.get( id ); }
	public int 		getPlayerCount() 	{ return players.size(); }
	public boolean	hasPlayer( int id )				{ return players.containsKey( id ); }
	public void 	addPlayer( int id, Player p ) 	{ players.put( id, p ); }
	public void 	removePlayer( int id ) 			{ players.remove( id ); }

	public int		getMinDistance( int player1, int player2 ) {
		int a = Math.abs(players.get(player1).getPosition()+(players.size()-players.get(player2).getPosition()));
		int b = Math.abs(players.get(player2).getPosition()-players.get(player1).getPosition());

		return Math.min(a, b);
	}
}
