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
	public static int 		timeout 	= 10;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;

	private Ping ping = new Ping();

	private AsynchronousSocketChannel socket;
	private final int id;
	private int packetSent = 0;

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

			if (packet.type == PacketType.PING) {
				if (ping.validate(packet)) {
					ping.stop();
					System.out.println("PING correct in time " + ping.getTime() + "ms");
				} else {
					System.err.println("Incorrect PING received!");
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
				
				/*
				 * Tää ei toimikkaan niinkuin luulin :(
				 * Jos lukeminen katkaistaan niin sitä ei voida enää jatkaa
				 * 
				 * Dokumentaatiosta:
				 * "Where a timeout occurs, and the implementation cannot guarantee that bytes have not been read, or will not be read from the 
				 * channel into the given buffer, then further attempts to read from the channel will cause an unspecific runtime exception to be thrown."
				 */
				
				Send(ping.start());
				StartReceive();

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
	
	public int getId() 
		{ return this.id; }
	
	private void StartWrite() 
		{ socket.write(messageQueue.poll().toByteBuffer(), timeout, timeunit, null, writeHandler); }
	
	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }
	
	public void Send(Packet packet) {
		System.out.println("Sending packet " + packet.type + " " + packet.from + "->" + packet.to);
		boolean writeInProgress = !messageQueue.isEmpty();
		messageQueue.add(packet);
		
		// Only one write per channel is possible
		if (!writeInProgress) {
			StartWrite();
			packetSent++;
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
