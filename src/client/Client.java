package client;

import game.GameContext;

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

	public static int		VERSION		= 0x1;
	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		BUFFER_SIZE	= 1024;
	
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;
	public static String	name		= "Unknown";

	final AsynchronousChannelGroup group;
	AsynchronousSocketChannel socket;
	
	private int id = -1;
	
	private ByteBuffer receiveBuffer;
	private Queue<Packet> packetQueue = new LinkedList<Packet>();
	boolean running = true;
	
	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			if (result == -1) {
				Disconnect();
				return;
			}
			
			Packet packet = new Packet(attachment);
			System.out.println("Received packet " + packet.getType() + " " + packet.getFrom() + "->" + packet.getTo());
			
			// Handle PING internally
			if (packet.getType() == PacketType.PING) {
				Send(packet); // FIXME: Inefficient to send 1024 bytes (only 18 bytes needed)
			} else {
				HandlePacket(packet);
			}

			StartReceive();
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			System.err.println("Failed to receive data: " + exc.toString());
			Disconnect();
		}
	};
	
	CompletionHandler<Integer, Object> writeHandler = new CompletionHandler<Integer, Object>() {
		@Override
		public void completed(Integer result, Object attachment) {
			System.out.println("Sent " + result + " bytes");
			if (!packetQueue.isEmpty()) {
				StartWrite();
			}
		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			System.out.println("Failed to send data:" + exc.toString());
			Disconnect();
		}
	};	



	private void HandlePacket(Packet packet) {
		switch(packet.getType()) {
		case SERVER_INFO:
			if (packet.getInt(0) != VERSION) {
				System.out.println("Server is using incorrect version " + packet.getInt(0));
				Disconnect();
			}
			
			this.id = packet.getTo();
			System.out.println("My ID: " + id);
			System.out.println("motd: " + packet.getString(4));
			break;
		case CLIENT_INFO:
			System.out.println("Player " + packet.getInt(0) + ": " + packet.getString(4));
			GameContext.AddPlayer(packet.getInt(0), packet.getString(4));
			break;
		case MSG:
			System.out.println(packet.getString(0));
			break;
		default:
			System.out.println("Unknown packet");
			break;

		
		}
	}
	
	private Client() throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());

		receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	}
	
	public void Connect(InetSocketAddress address) throws Exception{
		System.out.println("Connecting to " + address.getAddress() + ":" + address.getPort());
		
		socket = AsynchronousSocketChannel.open(group);
		if (socket.connect(address).get() != null) {
			System.err.println("Failed to connect");
		}
		
		StartReceive();
		Send(new ClientInfo(VERSION, name));
		
	}
	public void Disconnect() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("IOException while closing connection");
		}
	}
	
	private void Send(Packet packet) {
		System.out.println("Sending packet " + packet.getType() + " " + packet.getFrom() + "->" + packet.getTo());
		boolean writeInProgress = !packetQueue.isEmpty();
		packetQueue.add(packet);
		
		// Only one write per channel is possible
		if (!writeInProgress) {
			StartWrite();
		}
	}
	
	private void StartWrite() 
		{ socket.write(packetQueue.poll().toByteBuffer(), timeout, timeunit, null, writeHandler); }
	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }

	private void ClientLoop() throws Exception {
		String message = reader.nextLine();

		Send(new Packet(PacketType.MSG, id, 0, message));
		
		// Give server some time to respond => nicer console output 
		Thread.sleep(100);
	}

	public static void main(String[] args) {
		System.out.print("Use as server (y/n): ");
		String asServer = reader.nextLine();
		if (!asServer.isEmpty() && asServer.charAt(0) == 'y') {
			server.Server.main(args);
			return;
		}
		System.out.print("Address to connect (" + IP + "): ");
		String nIp = reader.nextLine();
		System.out.print("Port to connect (" + PORT + "): ");
		String nPort = reader.nextLine();

		if (!nPort.isEmpty()) PORT = Integer.parseInt(nPort);
		if (!nIp.isEmpty())   IP = nIp;
		
		name = (new Random()).nextBoolean() ? new String("Hyrsky") : new String("leonarven");
		System.out.println("name: " + name);
		
		try {
			Client client = new Client();
			client.Connect(new InetSocketAddress(IP, PORT));
			
			while(client.running) {	
				client.ClientLoop();
			}
			
		} catch(NotYetConnectedException e) {
			System.err.println("Connection to " + IP + ":" + PORT +" failed!");
		} catch (Exception e) {
			System.err.print("Fatal exception at Client::main(): ");
			System.err.println(e);
			e.printStackTrace();
		}
		
		System.out.println("Shutting down ...");
		reader.close();
	}
}
