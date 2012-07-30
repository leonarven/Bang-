package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.TimeUnit;

public class Connection {
	public static final int BUFFER_SIZE = 512;
	
	public static int 		timeout 	= 100;
	public static TimeUnit 	timeunit 	= TimeUnit.SECONDS;

	private AsynchronousSocketChannel socket;
	private final int id;

	private ByteBuffer receiveBuffer;

	CompletionHandler<Integer, ByteBuffer> receiveHandler = new CompletionHandler<Integer, ByteBuffer>() {
		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			byte[] bytearray = new byte[attachment.remaining()];
			attachment.get(bytearray);
			System.out.println("Received " + result + " bytes: " + new String(bytearray));
			
			// FIXME: Can this cause stack overflow? 
			// - Yes.
			StartReceive();
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			if (exc.getClass() == InterruptedByTimeoutException.class) {
				
				System.out.println("Timeout received - better ping the client.");
				
				// Ping client...
				// Restart receiving
				
				Server.instance.DropClient(id);
				
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
			System.out.println("Sent successfully");
		}

		@Override
		public void failed(Throwable exc, Object attachment) {
			System.out.println("Failed to send");
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
	
	private void StartWrite(ByteBuffer buffer)
		{ socket.write(buffer, timeout, timeunit, null, writeHandler); }
	
	private void StartReceive()
		{ socket.read(receiveBuffer, timeout, timeunit, receiveBuffer, receiveHandler); }
	
	public void Send(String message) throws CharacterCodingException {
		StartWrite(game.Engine.EncodeString(message));
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
