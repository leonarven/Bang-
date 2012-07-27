package client;

import java.net.*;
import java.nio.channels.*;
import java.util.concurrent.Executors;

public class Client {
	final AsynchronousChannelGroup group;
	
	private Client() throws Exception {
		group = AsynchronousChannelGroup.withThreadPool(Executors.newSingleThreadExecutor());
	}
	
	public void Connect(InetSocketAddress address) throws Exception{
		System.out.println("Connecting to " + address.getAddress().toString() + ":" + address.getPort());
		
		AsynchronousSocketChannel client = AsynchronousSocketChannel.open(group);
		client.connect(address);
		
		client.write(game.Engine.EncodeString("Hello server!"));
		
	}
	
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.Connect(new InetSocketAddress("localhost", 6667));
		
		} catch (Exception e) {
			System.out.println("Fatal exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
