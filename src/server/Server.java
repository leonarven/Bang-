package server;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

import network.Packet;
import network.PacketType;
import network.Ping;
import network.ServerInfo;

public class Server {
	public static Scanner reader = new Scanner(System.in);

	public static Server instance;
	
	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		BACKLOG		= 10;
	
	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;

	private int connectionCounter = 0;
	boolean running = true;
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

	public void HandlePacket(Packet packet) {
		System.out.println("Received packet " + packet.type + " " + packet.from + "->" + packet.to + ":" + packet);
		switch(packet.type) {
			case CHAT:
				SendToAll(packet);
			case PING:
				break;
			case MSG:
			default:
				System.out.println("MSG from client " + packet.from + ":" + packet);
				if (true) break; // FIXME
			case ILLEGAL: 
				System.err.println("ILLEGAL packet received!");
		}
	}

	public void SendToAll(Packet packet) {
		for (Connection c : this.connections.values())
			c.Send(packet);
	}
	
	public void DropClient(int id) {
		if (connections.containsKey(id)) {
			if (connections.get(id).IsConnected()) {
				System.out.println("Dropping client from: " + connections.get(id).GetRemoteAddress().toString());
			} else {
				System.out.println("Client has dc'd");
			}
			
			connections.remove(id);
		}
	}
	
	private void ServerLoop() throws Exception {
		System.out.println("Waiting for connection");
		try {
			AsynchronousSocketChannel socket = acceptor.accept().get();
			
			System.out.println("Incoming connection from " + socket.getRemoteAddress());
			
			// Send information about server:
			Connection connection = new Connection(++connectionCounter, socket);
			connection.Send(new ServerInfo(connectionCounter));
			for (game.Player p : game.GameContext.getPlayers()) {
				connection.Send(new network.ClientInfo(p.GetId(), p.getName()));	
			}
			
			connections.put(connection.getId(), connection);

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
			instance = new Server(IP, PORT);

			while (instance.running) {
				instance.ServerLoop();
			}
		} catch (Exception e) {
			System.out.print("Fatal exception at Server::main(): ");
			System.err.println(e);
			e.printStackTrace();
		}
		reader.close();
	}
}
