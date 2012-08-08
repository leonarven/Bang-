package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import network.*;

public class Connection {
	public static final int BUFFER_SIZE = 1024;
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;
	
	private final Server server;

	private Ping ping = new Ping();

	private AsynchronousSocketChannel socket;
	private final int id;

	private ByteBuffer receiveBuffer;
	private Queue<Packet> messageQueue = new LinkedList<Packet>();

	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			if (result == -1) {
				server.DropClient(id);
				return;
			}
			
			System.out.println("Received " + result + " bytes");

			//attachment.limit(result);
			Packet packet = new Packet(attachment);
			
			// Handle PING internally
			if (packet.getType() == PacketType.PING) {
				if (ping.validate(packet)) {
					ping.stop();
					System.out.println("PING correct in time " + ping.getTime() + "ms");
				} else {
					System.err.println("Incorrect PING received!");
					server.DropClient(id);
				}
			} else {
				server.HandlePacket(packet, Connection.this);
			}
			
			StartReceive();
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			
			/*
			 * Tää ei toimikkaan niinkuin luulin :(
			 * Jos lukeminen katkaistaan niin sitä ei voida enää jatkaa
			 * 
			 * Dokumentaatiosta:
			 * "Where a timeout occurs, and the implementation cannot guarantee that bytes have not been read, or will not be read from the 
			 * channel into the given buffer, then further attempts to read from the channel will cause an unspecific runtime exception to be thrown."
			 */
			
			System.out.println("Failed to receive data: " + exc.toString());
			server.DropClient(id);
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
			server.DropClient(id);
		}
	};
	
	public Connection(int id, AsynchronousSocketChannel socket, Server server) {
		this.id = id;
		this.socket = socket;
		this.server = server;

		receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		StartReceive();
		
		SendPing();
	}

	private void StartWrite() 
		{ socket.write(messageQueue.poll().toByteBuffer(), timeout, timeunit, null, writeHandler); }
	private void StartReceive() 
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }
	
	public void Send(Packet packet) {
		System.out.println("Sending packet " + packet.getType()  + " " + packet.getFrom() + "->" + packet.getTo());
		boolean writeInProgress = !messageQueue.isEmpty();
		messageQueue.add(packet);
		
		// Only one write per channel is possible
		if (!writeInProgress) {
			StartWrite();
		}
	}
	
	public void SendPing() {
		if (ping.isRunning())
			System.out.println("Warning: ping is still running");
		
		// FIXME: Ping is counting while it is in queue
		Send(ping.start());
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
	
	public long getLatency() 
		{ return ping.getTime(); }
	
	public int getId() 
		{ return this.id; }
}
