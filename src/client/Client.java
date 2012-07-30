package client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Scanner;
import java.util.concurrent.Executors;

import server.Server;


public class Client {
	public static Scanner reader = new Scanner(System.in);

	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		CAPACITY	= 1024;

	final AsynchronousChannelGroup group;
	AsynchronousSocketChannel socket;
	
	boolean running = true;
	
	ByteBuffer readBuffer = ByteBuffer.allocateDirect(CAPACITY);
	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			if (result == -1) {
				Disconnect();
				return;
			}
			
			attachment.flip();
			String message = game.Engine.DecodeString(attachment);
			System.out.println("Received " + result + " bytes: '" + message + "'");
			attachment.clear();
			
			//TODO: do own method for handling messages
			if (message.substring(0, 5) == "PING ") {
				message = "PING " + message.substring(5);
				Send(message);
			}
			
			socket.read(attachment, attachment, this);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			System.out.println("Failed to receive data: " + exc.toString());
		}
	};
	
	private Client() throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
	}
	
	public void Connect(InetSocketAddress address) throws Exception{
		System.out.println("Connecting to " + address.getAddress() + ":" + address.getPort());
		
		socket = AsynchronousSocketChannel.open(group);
		if (socket.connect(address).get() != null) {
			System.out.println("Failed to connect");
		}
		
		socket.read(readBuffer, readBuffer, receiveHandler);
		
	}
	public void Disconnect() {
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException while closing connection");
		}
	}
	
	private int Send(ByteBuffer buffer) {
		int bytesSent = -1;
		try {
			bytesSent = socket.write(buffer).get();
			if (bytesSent == -1) {
				System.err.println("Failed to send data");
				Disconnect();
			} 
		} catch(Exception e) {
			System.err.println("Cannot send buffer!");
		}
		return bytesSent;
	}
	private int Send(String buffer)
		{ return Send(game.Engine.EncodeString(buffer)); }

	private void ClientLoop() throws Exception {
		
		System.out.print("> ");
		String message = reader.nextLine();
		ByteBuffer buffer = game.Engine.EncodeString(message);

		Send(buffer);
		
		// Give server some time to respond => nicer console output 
		Thread.sleep(100);
	}

	public static void main(String[] args) {
		System.out.print("Use as server (y/n): ");
		if (reader.nextLine().charAt(0) == 'y') {
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
