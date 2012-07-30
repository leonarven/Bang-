package server;


import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
	public static Scanner reader = new Scanner(System.in);

	public static Server instance;
	
	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		BACKLOG		= 10;
	public static int		CAPACITY	= 1024;

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
			System.out.println("Failed to open acceptor");
		}
		
		connections = new HashMap<Integer, Connection>();
		
		System.out.println("Listening to " + ip + ":" + port);
	}
	
	public void NegotiateClient() {
		/* Before connection gets accepted into a game:
		 * > Send 		Hello Message
		 * > Send 		Version
		 * > Send 		Ping
		 * < Receive 	Ping
		 * < Receive 	ClientInfo <Nickname,Version,...>
		 * 
		 * Add Player to game
		 * Otherwise timeout and drop the client
		 */
		
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
			//AsynchronousSocketChannel socket = AsynchronousSocketChannel.open(group);
			AsynchronousSocketChannel socket = acceptor.accept().get();
			
			System.out.println("Incoming connection from " + socket.getRemoteAddress());
			connections.put(++connectionCounter, new Connection(connectionCounter, socket));

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
