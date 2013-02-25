package server;

import network.Packet;
import game.Game;
import game.Player;

public class ServerLogic extends Game {

	public final Server server;
	
	public ServerLogic(Server server) {
		this.server = server;
		
	}

	@Override
	public void handlePacket(Packet packet) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doStart() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doReset() {
		// TODO Auto-generated method stub
	}
	
	// Return true if there is enough players and everyone is ready
	private boolean readyToStart() {
		if ( players.size() < minPlayers )
			return false;
		
		for ( Player p : players.values() ) {
			if ( !p.isReady() )
				return false;
		}
		
		return true;
	}
}
