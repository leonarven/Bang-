package client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Scanner;
import java.util.concurrent.Executors;


public class Client {
	public static Scanner reader = new Scanner(System.in);

	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		CAPACITY	= 1024;

	final AsynchronousChannelGroup group;
	AsynchronousSocketChannel client;
	
	boolean running = true;
	
	private Client() throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
	}
	
	public void Connect(InetSocketAddress address) throws Exception{
		System.out.println("Connecting to " + address.getAddress() + ":" + address.getPort());
		
		client = AsynchronousSocketChannel.open(group);
		if (client.connect(address).get() != null) {
			System.out.println("Failed to connect");
		}
		
		Thread.sleep(1000);

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
		ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

		int bytesSent = client.write(buffer).get();
		if (bytesSent != -1) {
			System.out.println("Sent " + bytesSent + " bytes");
		} else {
			System.out.println("Failed to send data");
			Disconnect();
		}
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
