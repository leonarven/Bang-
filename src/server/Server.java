package server;

import game.GameContext;
import game.Player;

import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import network.*;

public class Server {
	public static Scanner reader = new Scanner(System.in);
	
	public static final int 	VERSION 	= 0x1;
	public static int			PORT		= 6667;
	public static String		IP			= "127.0.0.1";
	public static int			BACKLOG		= 10;
	
	private boolean running = true;
	
	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;

	private int connectionCounter = 0;
	private HashMap<Integer, Connection> connections;
	
	private Server(String ip, int port) throws Exception {
		System.out.println("Initializing server (at " + ip + ":" + port + ") ...");

		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
		acceptor = AsynchronousServerSocketChannel.open(group);
		acceptor.bind(new InetSocketAddress(ip, port), BACKLOG);
		
		if (!acceptor.isOpen()) {
			System.err.println("Failed to open acceptor");
		}
		
		connections = new HashMap<Integer, Connection>();
		
		System.out.println("Listening to " + ip + ":" + port);
	}

	public void HandlePacket(Packet packet, Connection connection) {
		System.out.println("Received packet " + packet.getType() + " " + packet.getFrom() + "->" + packet.getTo());
		
		if (packet.getType() == PacketType.CLIENT_INFO) {
			
			if (packet.getInt(0) != VERSION) {
				System.out.println("Client is using incorrect version " + packet.getInt(0));
				DropClient(connection.getId());
			}

			// Client sent correct information -> accept to game
			
			System.out.println(packet.getData());
			
			String name = packet.getString(4);
			System.out.println("Name: '" + name + "'");
			SendToAll(new ClientInfo(connection.getId(), name));

			GameContext.AddPlayer(connection.getId(), name);
			connection.Send(new ServerInfo(VERSION, connection.getId()));
			for (Player p : GameContext.getPlayers())
				connection.Send(new ClientInfo(p));
			
			SendToAll(new Packet(PacketType.MSG, 0, 0, "Welcome " + name + "!"));
			
		} else {
			
			// Don't use packet.getFrom(), use connection.getId()
			
			System.out.println("Error: Didn't receive client info, id " + connection.getId());
			DropClient(connection.getId());
		}
	}

	public void SendToAll(Packet packet) {
		for (Player p : GameContext.getPlayers())
			this.connections.get(p.GetId()).Send(packet);
	}
	
	public void DropClient(int id) {
		if (connections.containsKey(id)) {
			if (connections.get(id).IsConnected()) {
				System.out.println("Dropping client from: " + connections.get(id).GetRemoteAddress().toString());
			} else {
				System.out.println("Client has dc'd");
			}
			
			if (GameContext.getPlayer(id) != null)
				GameContext.RemovePlayer(id);
			
			connections.remove(id);
		}
	}
	
	private void ServerLoop() throws Exception {
		System.out.println("Waiting for connection");
		try {
			AsynchronousSocketChannel socket = acceptor.accept().get();
			
			System.out.println("Incoming connection from " + socket.getRemoteAddress());
			
			Connection connection = new Connection(++connectionCounter, socket, this);
			connections.put(connection.getId(), connection);
			
			
			//connection.Send(new ServerInfo(connectionCounter));
			//for (game.Player p : game.GameContext.getPlayers()) {
			//	connection.Send(new network.ClientInfo(p.GetId(), p.getName()));	
			//}

		} catch ( Exception e ) {

			System.out.print("Fatal exception as Server::ServerLoop(): ");
			System.err.println(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.print("Address to listen (" + IP + "): ");
		String nIp = reader.nextLine();
		System.out.print("Port to listen (" + PORT + "): ");
		String nPort = reader.nextLine();

		if (!nPort.isEmpty()) PORT = Integer.parseInt(nPort);
		if (!nIp.isEmpty())   IP = nIp;
		try {
			Server server = new Server(IP, PORT);

			while (server.running) {
				server.ServerLoop();
			}
		} catch (Exception e) {
			System.out.print("Fatal exception at Server::main(): ");
			System.err.println(e);
			e.printStackTrace();
		}
		reader.close();
	}
}
