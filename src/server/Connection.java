package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.*;
import java.util.concurrent.*;

import network.Packet;

public class Connection {
	public static final int BUFFER_SIZE = 512;
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;
	
	private final Server server;
	private final AsynchronousSocketChannel socket;
	private final int id;

	private BlockingQueue<Packet> 	inQueue = new LinkedBlockingQueue<Packet>();
	private Queue<Packet> 			outQueue = new LinkedList<Packet>();
	
	public Connection( int id, AsynchronousSocketChannel socket, Server server ) {
		this.id = id;
		this.socket = socket;
		this.server = server;
		
		startRead( ByteBuffer.allocateDirect(BUFFER_SIZE) );
	}
	
	private void startRead( ByteBuffer receiveBuffer ) {
		socket.read( receiveBuffer, timeout, timeunit, receiveBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) { 
				if ( result > 0 ) {
					
					inQueue.add( new Packet( attachment ) );
					
					// ReceiveBuffer might get modified right after this function
					Connection.this.startRead( attachment );
				} else {
					Connection.this.server.dropConnection( Connection.this );
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				try {
					System.err.println("Failed to send data:" + exc.toString());
					Connection.this.server.dropConnection( Connection.this );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private synchronized void startWrite() {
		// Keep sending data until outQueue is empty
		if ( !outQueue.isEmpty() ) {
			socket.write( outQueue.poll().toByteBuffer(), timeout, timeunit, null, new CompletionHandler<Integer, Object>() {
				@Override
				public void completed(Integer result, Object attachment) 
					{ Connection.this.startWrite(); }
	
				@Override
				public void failed( Throwable exc, Object attachment ) {
					try {
						System.err.println("Failed to send data:" + exc.toString());
						Connection.this.server.dropConnection( Connection.this );
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public synchronized void send( Packet packet ) {
		boolean writeInProgress = !outQueue.isEmpty();
		outQueue.add( packet  );
		
		// Only one write per channel is possible
		if ( !writeInProgress ) {
			startWrite();
		}
	}

	public boolean hasReceivedData() 
		{ return inQueue.isEmpty(); }
	
	// Allways check if return value is null
	public Packet receive()
		{ return inQueue.poll(); }

	public InetSocketAddress getRemoteAddress() {
		try {
			return (InetSocketAddress) socket.getRemoteAddress();
		} catch ( IOException e ) {
			e.printStackTrace( System.err );
			return null;
		}
	}
	
	public boolean isConnected() {
		return socket.isOpen();
	}
	
	public int getId() 
		{ return this.id; }
}
