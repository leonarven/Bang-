package client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import network.*;
import server.Server;


public class Client {
	public static Scanner reader = new Scanner(System.in);

	public static int		VERSION		= 0x1;
	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		BUFFER_SIZE	= 1024;
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;
	public static String	name		= "Unknown";

	public static int		STATUS	= 1024;

	final AsynchronousChannelGroup group;
	AsynchronousSocketChannel socket;
	
	private int id;

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
			System.out.println("Received packet " + packet.type + " " + packet.from + "->" + packet.to + ":" + packet.data);
			System.out.println("Received data: " + packet);

			StartReceive();

			HandlePacket(packet);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			System.err.println("Failed to receive data: " + exc.toString());
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

	private void StartWrite() 
		{ socket.write(packetQueue.poll().toByteBuffer(), timeout, timeunit, null, writeHandler); }

	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }

	private void HandlePacket(Packet packet) {
		switch(packet.type) {
			case MSG:
			case CHAT:
				System.out.println(packet);
				break;
			case PING:
				Send(packet);
				break;
			case SERVER_INFO:
				break;
			case CLIENT_INFO:
				this.id = new ClientInfo(packet).getId();
				break;
			case GAME_STATUS:
				break;
			case ILLEGAL: default:
				System.err.println("ILLEGAL packet received!");
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
		
		socket.read(receiveBuffer, receiveBuffer, receiveHandler);
		
	}
	public void Disconnect() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("IOException while closing connection");
		}
	}
	
	private int Send(Packet packet) {
		System.out.println("Sending " + packet);
		int bytesSent = -1;
		try {
			bytesSent = socket.write(packet.toByteBuffer()).get();
			if (bytesSent == -1) {
				System.err.println("Failed to send data");
				Disconnect();
			} 
		} catch(Exception e) {
			System.err.println("Cannot send buffer!");
		}
		return bytesSent;
	}

	private void ClientLoop() throws Exception {
		
		System.out.print("> ");
		String message = reader.nextLine();

		Send(new Packet('C', 0, 0, message));
		
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
		
		try {
			Client client = new Client();

			client.packetQueue.add(new ClientInfo(VERSION, name));
			
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
