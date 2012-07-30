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
	AsynchronousSocketChannel client;
	
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
			
			client.read(attachment, attachment, this);
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
		
		client = AsynchronousSocketChannel.open(group);
		if (client.connect(address).get() != null) {
			System.out.println("Failed to connect");
		}
		
		client.read(readBuffer, readBuffer, receiveHandler);
		
	}
	public void Disconnect() {
		running = false;
		try {
			client.close();
		} catch (IOException e) {
			System.out.println("IOException while closing connection");
		}
	}

	private void ClientLoop() throws Exception {
		
		System.out.print("> ");
		String message = reader.nextLine();
		ByteBuffer buffer = game.Engine.EncodeString(message);

		int bytesSent = client.write(buffer).get();
		if (bytesSent == -1) {
			System.out.println("Failed to send data");
			Disconnect();
		}
		
		// Give server some time to respond => nicer console output 
		Thread.sleep(100);
	}

	public static void main(String[] args) {
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
