package server;

import java.util.ArrayList;
import java.util.Collections;

import network.IntPacket;
import network.JsonPacket;
import network.Packet;
import network.PacketType;
import network.StringPacket;
import game.Game;
import game.JSONObject;
import game.Player;
import game.PlayerType;

public class ServerLogic extends Game {

	public final Server server;
	
	public ServerLogic(Server server) {
		this.server = server;
		
	}

	@Override
	public void handlePacket( Packet packet ) {

		/* Jaakko, älä katso tätä..
		 */
		
		int cId = packet.toByteBuffer().getInt(2);
		Connection connection = null;
		
		for ( Connection c : server.connections )
			if ( cId == c.getId() ) { connection = c; break; }

		/* Jaakko, nyt saa avata silmät taas
		 */

		try {
			
			// Returns null if there is no player with that id
			Player player = players.get( connection.getId() );
	
			if ( packet.getType() != PacketType.PING )
				System.out.println( "DEBUG: PacketType "+packet.getType().toChar()+" read from #"+connection.getId() );
	
			if ( player != null || packet.getType() == PacketType.CLIENT_INFO ) {
	
				switch(packet.getType()) {
				case MSG:
					StringPacket messagePacket = new StringPacket( packet );
					System.out.println( "CHAT: <" + player.getName() + "> " + messagePacket.getData() );
					server.sendToAll( packet );
					break;
				case READY:
					IntPacket readyPacket = new IntPacket( packet );
					if (readyPacket.getData() == 0) player.setReady( false );
					else {
						player.setReady( true );
	
						// TODO: message(character_id):n persuteella valikoitu hahmo
						game.Character character = new game.Character("Unknown", 3);
						player.setCharacter( character );
	
						server.sendToAll( packet );
					}
					if ( readyToStart() ) {
						this.start(); // Start game when everyone is ready
					}
					break;
				case CLIENT_INFO:
					StringPacket clientInfo = new StringPacket( packet );
	
					if ( player == null ) {
						// Uusi pelaaja
						
						if ( players.size() >= maxPlayers ) {
							System.out.println("DEBUG: Server is full - Dropping client");
							server.dropConnection( connection ); // FIXME: Server is full
							break;
						}

						connection.setPlayer(player = new Player(connection.getId(), clientInfo.getData()));
					}
					
					server.sendToAll( packet );
					for (Player p : players.values()) {
						connection.send(new StringPacket(PacketType.CLIENT_INFO, p.getId(), p.getName()).toPacket());

						if (p.isReady())
							connection.send(new IntPacket(PacketType.READY, p.getId(), 1).toPacket());
					}
					players.put( connection.getId(), player );
					break;
				case ERROR:
				default:
					break;
				}
			} else {
				server.dropConnection( connection ); // Client obviously doesn't know how to speak to server...
			}
		} catch(Exception e) {
			System.out.println("ERROR: Server:Game:handlePacket()");
			System.out.println(e.getMessage());
		}	}

	@Override
	protected void doStart() {
		this.isRunning = true; 
		
		System.out.println( "Game is starting!" );
		
		// TODO: LÄhetä pelaajien järjestys

		// TODO: Remove clients who don't have player
		for( Connection c : server.connections ) {
			if ( c.player == null ) server.dropConnection(c);
		}

		int renegadeCount = 0;//((int)(server.connections.size()/4 - 0.5));
		int deputyCount   = 0;
		int outlawCount   = 0;
		
		ArrayList<PlayerType> playerTypePile = new ArrayList<PlayerType>();
		
		//TODO: asetuksista
		switch(server.connections.size()) {

		
			case 12: renegadeCount++;
			case 11: outlawCount  ++;
			case 10: deputyCount  ++;
		
			case 9: outlawCount   ++;
			case 8: renegadeCount ++;
			case 7: deputyCount   ++;

			case 6: outlawCount   ++;
			case 5: deputyCount   ++;
			case 4: renegadeCount ++;
			
			case 3: outlawCount   ++;
			case 2: outlawCount   ++;
			case 1:
			break;
			default:
				assert false;
		}

		for( int i = 0; i < outlawCount; i++)   playerTypePile.add(PlayerType.OUTLAW);
		for( int i = 0; i < deputyCount; i++)   playerTypePile.add(PlayerType.DEPUTY);
		for( int i = 0; i < renegadeCount; i++) playerTypePile.add(PlayerType.RENEGADE);

		Collections.shuffle(playerTypePile);
		
		playerTypePile.add(PlayerType.SHERIFF);
		
		int playerTypePileI = 0;

		for( Connection c : server.connections ) {
			// TODO: READY-paketti pelaajille, tieto kunkin roolista

			JSONObject playerInfoPublicJson = new JSONObject();
			JSONObject playerInfoPrivateJson = new JSONObject();

			c.player.setType(playerTypePile.get(playerTypePileI++));
			
			playerInfoPublicJson.put("health", c.player.getHealth());
			playerInfoPublicJson.put("range", c.player.getRange());
			playerInfoPublicJson.put("characterName", c.player.getCharacter().GetName());
			if ( c.player.getType() == PlayerType.SHERIFF )
				playerInfoPublicJson.put("playerType", c.player.getType().toChar());

			playerInfoPrivateJson.put("health", c.player.getHealth());
			playerInfoPrivateJson.put("range", c.player.getRange());
			playerInfoPrivateJson.put("characterName", c.player.getCharacter().GetName());
			playerInfoPrivateJson.put("playerType", c.player.getType().toChar());
			
			JsonPacket playerInfoPublic = new JsonPacket(PacketType.PLAYER_INFO, c.getId(), playerInfoPublicJson);
			JsonPacket playerInfoPrivate = new JsonPacket(PacketType.PLAYER_INFO, c.getId(), playerInfoPrivateJson);

			server.sendToAllBut(c.getId(), playerInfoPublic.toPacket());
			c.send(playerInfoPrivate.toPacket());
		}
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

	@Override
	protected void preTurn() {
		// TODO Auto-generated method stub
		
	}
}
