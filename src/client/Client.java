package client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

import network.*;

public class Client {
	public static Scanner reader = new Scanner(System.in);

	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		BUFFER_SIZE	= 512;
	
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;
	public static String	name		= "Unknown";
	
	private Game game;

	private final AsynchronousChannelGroup group;
	private AsynchronousSocketChannel socket;

	private Queue<Packet> packetQueue = new LinkedList<Packet>();
	private boolean running = true;

	private Client() throws IOException {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
	}
	
	public void connect(InetSocketAddress address, ByteBuffer buffer) throws Exception{
		System.out.println("Connecting to " + address.getAddress() + ":" + address.getPort());

		socket = AsynchronousSocketChannel.open(group);
		if (socket.connect(address).get() != null) { // Blocking!
			System.err.println("Failed to connect");
		}

		int localPlayer = 0;
		
		// Wait for server info
		if (socket.read( buffer ).get() > 0) {
			ServerInfo server = new ServerInfo( new Packet( buffer ));
			localPlayer = server.getId();
			game = new Game( localPlayer );
			System.out.println( "DEBUG: SERVER_INFO: v."+server.VERSION );
		}

		// Start receiving packets in another thread:
		startRead(buffer);

		send(new ClientInfo(localPlayer, Integer.toString(new Random().nextInt())).toPacket());
		
	}

	private void clientLoop() {
		System.out.print("> ");
		String message = reader.nextLine();
		
		this.send(new Message(game.getLocalPlayerId(), message).toPacket());
		
		// Give server some time to respond => nicer console output 
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	public void disconnect() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("IOException while closing connection");
		}
	}
	
	private void send(Packet packet) {
		boolean writeInProgress = !packetQueue.isEmpty();
		packetQueue.add(packet);

		if (packet.getType() != PacketType.PING)
			System.out.println("DEBUG: Sending packet ("+packet.getType().toChar()+")");
		
		// Only one write per channel is possible
		if (!writeInProgress) {
			startWrite();
		}
	}
	
	private void startWrite() {
		if ( !packetQueue.isEmpty() ) {
			socket.write(packetQueue.poll().toByteBuffer(), timeout, timeunit, null, new CompletionHandler<Integer, Object>() {
				@Override
				public void completed(Integer result, Object attachment) 
					{ Client.this.startWrite(); }
	
				@Override
				public void failed(Throwable exc, Object attachment) {
					System.out.println("Failed to send data:" + exc.toString());
					Client.this.disconnect();
				}
			});
		}
	}

	private void startRead( ByteBuffer receiveBuffer ) {
		receiveBuffer.clear();
		
		socket.read( receiveBuffer, timeout, timeunit, receiveBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) { 
				if ( result > 0 ) {
					// Packet ctor makes a copy of ByteBuffers data 
					// so it can be modified after this:
					Packet packet = new Packet(attachment);
					
					// TODO: Siirrä handlePacketiin
					if ( packet.getType() == PacketType.PING ) {
						send(packet);
					} else game.handlePacket(packet); // Pingia tarvitse käsitellä

					Client.this.startRead( attachment );
				} else {
					System.err.println( "Invalid result from read: " + result );
					Client.this.disconnect();
				}
								
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.err.println( "Failed to read data:" + exc.toString() );
				Client.this.disconnect();
			}
		});
	}

	public static void main(String[] args) {
		System.out.print("Use as server (y/N): ");
		String asServer = reader.nextLine();
		if (!asServer.isEmpty() && asServer.charAt(0) == 'y') {
			server.Server.main(args);
			return;
		}
		
		System.out.print("Address to connect (" + IP + "): ");
		String nIp = reader.nextLine();
		System.out.print("Port to connect (" + PORT + "): ");
		String nPort = reader.nextLine();

		if (!nPort.isEmpty()) PORT 	= Integer.parseInt(nPort);
		if (!nIp.isEmpty())   IP 	= nIp;

		try {
			Client client = new Client();
			ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
			client.connect(new InetSocketAddress(IP, PORT), buffer);
			
			while(client.running) {	
				client.clientLoop();
			}
			
		} catch (Exception e) {
			// TODO: Siistimmäksi. Tieto, jos serveriä ei tavoiteta (java.util.concurrent.ExecutionException: java.net.ConnectException)
			// TODO: Tieto, jos serveri tippuu (java.nio.channels.InterruptedByTimeoutException)
			
			System.err.print("Fatal exception at Client::main(): ");
			System.err.println(e);
			e.printStackTrace();
		}
		
		System.out.println("Shutting down ...");
		reader.close();
	}
}
