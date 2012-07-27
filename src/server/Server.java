package server;


import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.Executors;

public class Server {
	public static Server instance;
	
	public static int PORT = 6667;
	public static final int BACKLOG = 10;

	final AsynchronousChannelGroup group;
	final AsynchronousServerSocketChannel acceptor;
	
	private int connectionCounter = 0;
	private HashMap<Integer, Connection> connections;

	private Server(int port) throws Exception {
		System.out.println("Initializing server (at port " + PORT + ") ...");

		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
		acceptor = AsynchronousServerSocketChannel.open(group);
		acceptor.bind(new InetSocketAddress(port), BACKLOG);
		
		connections = new HashMap<Integer, Connection>();
		
		System.out.println("... Ready!");
		System.out.println("Listening to port " + port);
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
			System.out.println("Dropping client from: " + connections.get(id).GetRemoteAddress().toString());
			connections.remove(id);

			//FIXME: For some reason connections don't get destroyed
		}
	}
	
	private void ServerLoop() throws Exception {
		System.out.println(connectionCounter);
		AsynchronousSocketChannel socket = acceptor.accept().get();
		
		connections.put(++connectionCounter, new Connection(connectionCounter, socket));
		
		System.out.println("Connection received from: " +  connections.get(connectionCounter).GetRemoteAddress().toString());
	}
	
	public static void main(String[] args) {
		System.out.print("Port to listen (empty to default): ");
		Scanner reader = new Scanner(System.in);
		String nPort = reader.nextLine();
		if (!nPort.isEmpty()) PORT = Integer.parseInt(nPort);
		try {
			instance = new Server(PORT);

			System.out.println("Starting ServerLoop.");
			while (true) {
				instance.ServerLoop();
			}
		} catch (Exception e) {
			System.out.println("Fatal exection: " + e.toString());
			e.printStackTrace();
		}
		reader.close();
	}
}
