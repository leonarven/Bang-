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
	
		// Allocate once for big packet. When packet is received, 
		// copy the relevant information from the buffer.
		startRead( ByteBuffer.allocateDirect(BUFFER_SIZE) );
	}
	
	public void close() throws IOException {
		this.socket.close();
	}
	
	private void startRead( ByteBuffer receiveBuffer ) {
		// This might have some bugs. see: https://en.wikipedia.org/wiki/Producer-consumer_problem
		socket.read( receiveBuffer, timeout, timeunit, receiveBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				// The result passed to the completion handler is the number of bytes read or -1 
				// if no bytes could be read because the channel has reached end-of-stream. 
				if ( result > 0 ) {
					// Packet ctor makes a copy of ByteBuffers data 
					// so it can be modified after this:
					inQueue.add( new Packet( attachment ) ); // Assuming main thread consumes packets faster than they are received

					attachment.clear();					
					Connection.this.startRead( attachment );
				} else {
					System.err.println( "Invalid result from read: " + result );
					Connection.this.server.dropConnection( Connection.this );
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.err.println( "Failed to read data: " + exc.toString() );
				Connection.this.server.dropConnection( Connection.this );
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
					System.err.println("Failed to send data: " + exc.toString());
					Connection.this.server.dropConnection( Connection.this );
				}
			});
		}
	}
	
	public synchronized void send( Packet packet ) {
		boolean writeInProgress = !outQueue.isEmpty();
		outQueue.add( packet  );

		System.out.println("DEBUG: Sending packet ("+packet.getType().toChar()+")");
		
		// Only one write per channel is possible
		if ( !writeInProgress ) {
			startWrite();
		}
	}

	public boolean hasReceivedData() 
		{ return !inQueue.isEmpty(); }
	
	// Warning: This function is blocking if there is no data in the queue
	public Packet receiveBlocking() throws InterruptedException
		{ return inQueue.take(); }
	
	// This function returns null if there is no data in the queue
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
