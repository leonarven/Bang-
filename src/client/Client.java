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

	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		CAPACITY	= 1024;
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;

	public static int		STATUS	= 1024;

	final AsynchronousChannelGroup group;
	AsynchronousSocketChannel socket;
	
	private int id;

	private ByteBuffer receiveBuffer;
	private Queue<Packet> packetQueue = new LinkedList<Packet>();
	boolean running = true;
	
	ByteBuffer readBuffer = ByteBuffer.allocateDirect(CAPACITY);
	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			if (result == -1) {
				Disconnect();
				return;
			}
			
			Packet packet = new Packet(attachment);
			StartReceive();

			socket.read(attachment, attachment, this);

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
			Server.instance.DropClient(id);
		}
	};	

	private void StartWrite() 
		{ socket.write(packetQueue.poll().toByteBuffer(), timeout, timeunit, null, writeHandler); }

	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }

	private void HandlePacket(Packet packet) {
		System.out.println("Received packet " + packet.type + " " + packet.from + "->" + packet.to + ":" + packet);
		switch(packet.type) {
			case MSG:
			case CHAT:
				System.out.println(packet);
				break;
			case PING:
				Send(packet);
				break;
			case ILLEGAL: default:
				System.err.println("ILLEGAL packet received!");
		}
	}
	
	private Client() throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
	}
	
	public void Connect(InetSocketAddress address) throws Exception{
		System.out.println("Connecting to " + address.getAddress() + ":" + address.getPort());
		
		socket = AsynchronousSocketChannel.open(group);
		if (socket.connect(address).get() != null) {
			System.err.println("Failed to connect");
		}
		
		socket.read(readBuffer, readBuffer, receiveHandler);
		
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
		Packet p;
		p = new Packet(PacketType.MSG, 1, 2, "Foobar");
		System.out.println("packet: " + p.type + " " + p.from + "->" + p.to + ":" + p);

		p = new Packet(PacketType.ILLEGAL, 2, 3, "Foobar".getBytes());
		System.out.println("packet: " + p.type + " " + p.from + "->" + p.to + ":" + p);

		p = new Packet('a', 3, 4, ByteBuffer.wrap("Foobar".getBytes()));
		System.out.println("packet: " + p.type + " " + p.from + "->" + p.to + ":" + p);

		p = new Packet('R', 4, 5, ByteBuffer.allocate(1014));
		System.out.println("packet: " + p.type + " " + p.from + "->" + p.to + ":" + p);

		if(true) return;
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
			
/*			ByteBuffer clientInfo;
			clientInfo.pushInt(VERSION);
			clientInfo.pushBytes("name".getBytes());

			packetQueue.add(new Packet(PacketType.CLIENT_INFO, 0, 0, clientInfo));
	*/		
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
