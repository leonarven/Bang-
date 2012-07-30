package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import network.*;

public class Connection {
	public static final int BUFFER_SIZE = 1024;
	
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;

	private Ping ping = new Ping();

	private AsynchronousSocketChannel socket;
	private final int id;

	private ByteBuffer receiveBuffer;
	private Queue<String> messageQueue = new LinkedList<String>();

	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			if (result == -1) {
				Server.instance.DropClient(id);
				return;
			}

			attachment.flip();
			String message = game.Engine.DecodeString(attachment);
			System.out.println("Received " + result + " bytes: '" + message + "'");
			attachment.clear();
			StartReceive();
			
			//TODO: do own method for handling messages
			if (message.substring(0, 5) == "PONG ") {
				if (message.substring(5) == Long.toString(ping.getMagicNumber())) {
					ping.stop();
				} else {
					System.err.println("Wrong PONG -magic number received from client " + id);
					Server.instance.DropClient(id);
				}
			}
			
			Send(message);
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			if (exc.getClass() == InterruptedByTimeoutException.class) {
				
				System.out.println("Timeout received - better ping the client.");
				
				ping.start();
				Send("PING " + ping.getMagicNumber());
			} else {
				System.out.println("Failed to receive data: " + exc.toString());
				exc.printStackTrace();
				Server.instance.DropClient(id);
			}
		}
	};
	CompletionHandler<Integer, Object> writeHandler = new CompletionHandler<Integer, Object>() {
		@Override
		public void completed(Integer result, Object attachment) {
			System.out.println("Sent " + result + " bytes");
			if (!messageQueue.isEmpty()) {
				StartWrite();
			}
		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			System.out.println("Failed to send data:" + exc.toString());
			Server.instance.DropClient(id);
		}
	};
	
	public Connection(int id, AsynchronousSocketChannel socket) {
		this.id = id;
		this.socket = socket;

		receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		
		StartReceive();
	}
	
	protected void finalize() {
		System.out.println("Finalizing connection: " + id);
		try {
			socket.close();
		} catch (Exception e) {}
	}
	
	private void StartWrite() 
		{ socket.write(game.Engine.EncodeString(messageQueue.poll()), timeout, timeunit, null, writeHandler); }
	
	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }
	
	public void Send(String message) {
		boolean writeInProgress = !messageQueue.isEmpty();
		messageQueue.add(message);
		
		// Only one write per channel is possible
		if (!writeInProgress) {
			StartWrite();
		}
	}
	
	public boolean IsConnected() {
		return socket.isOpen();
	}
	
	public InetSocketAddress GetRemoteAddress() {
		try {
			return (InetSocketAddress) socket.getRemoteAddress();
		} catch (IOException e) {
			
			return null;
		}

	}
}
