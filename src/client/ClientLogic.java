package client;

import network.Packet;
import game.*;

public class ClientLogic extends Game {
	private final Client client;
	private final int localPlayerId;
	
	public ClientLogic(Client client, int localPlayerId) {
		this.client 		= client;
		this.localPlayerId 	= localPlayerId;
	}

	public int getLocalPlayerId()
		{ return localPlayerId; }
	
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

}
