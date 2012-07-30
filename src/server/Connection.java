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
	private Queue<Packet> messageQueue = new LinkedList<Packet>();

	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			if (result == -1) {
				Server.instance.DropClient(id);
				return;
			}

			Packet packet = new Packet(attachment);
			StartReceive();
			
			Server.instance.HandlePacket(packet);

			if (packet.getType() == PacketType.PING) {
				if (ping.validate(packet)) {
					ping.stop();
					System.out.println("PING correct in time " + ping.getTime() + "!");
				} else {
					System.err.println("Wrong PING received!");
					Server.instance.DropClient(id);
				}
			} else {
				Send(packet);
			}
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			if (exc.getClass() == InterruptedByTimeoutException.class) {
				
				System.out.println("Timeout received - better ping the client.");
				Send(ping.start());

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
		
		Send(ping.start());

		StartReceive();
	}
	
	protected void finalize() {
		System.out.println("Finalizing connection: " + id);
		try {
			socket.close();
		} catch (Exception e) {}
	}
	
	private void StartWrite() 
		{ socket.write(messageQueue.poll().toByteBuffer(), timeout, timeunit, null, writeHandler); }
	
	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }
	
	public void Send(Packet packet) {
		System.out.println("Sending packet " + packet.getType() + " " + packet.getFrom() + "->" + packet.getTo() + ":" + packet);
		boolean writeInProgress = !messageQueue.isEmpty();
		messageQueue.add(packet);
		
		// Only one write per channel is possible
		if (!writeInProgress) {
			StartWrite();
		} else {
			System.out.println("Pushing to queue.");
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
