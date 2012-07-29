package client;

import java.util.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Scanner;
import java.util.concurrent.Executors;

import server.Connection;
import server.Server;

public class Client {
	public static Scanner reader = new Scanner(System.in);

	public static int		PORT		= 6667;
	public static String	IP			= "127.0.0.1";
	public static int		CAPACITY	= 1024;

	final AsynchronousChannelGroup group;
	
	boolean running = true;
	
	private Client() throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
	}
	
	public void Connect(InetSocketAddress address) throws Exception{
		System.out.println("Connecting to " + address.getAddress() + ":" + address.getPort());
		
		AsynchronousSocketChannel client = AsynchronousSocketChannel.open(group);
		client.connect(address);
		
		client.write(game.Engine.EncodeString("Hello server!"));
	}
	public void Disconnect() {
		
	}

	private void ClientLoop() throws Exception {
/*		try(AsynchronousSocketChannel socket = client.accept().get()) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(CAPACITY);
			
			while(socket.read(buffer).get() != -1) {

			}

		} catch ( Exception e ) {
			System.out.print("Fatal exception as Client::ClientLoop(): ");
			System.err.println(e);
			e.printStackTrace();
		}*/
	}

	public static void main(String[] args) {
		System.out.print("Address to connect (" + IP + "): ");
		String nPort = reader.nextLine();
		System.out.print("Port to connect (" + PORT + "): ");
		String nIp = reader.nextLine();

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
		System.out.println("... Ready!");
	}
}
