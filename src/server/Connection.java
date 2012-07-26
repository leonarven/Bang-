package server;

import java.net.*;
import java.nio.channels.*;
import java.util.concurrent.Executors;

public class Connection {
	AsynchronousSocketChannel socket;
	
	public Connection(AsynchronousSocketChannel socket) {
		this.socket = socket;
		
		//If a timeout is specified and the timeout elapses before the operation completes then 
		//the operation completes with the exception InterruptedByTimeoutException
		// -> Ping the client to see if they are connected
		socket.read(dst, attachment, handler)
		
	}
	
	private void StartSend() {
		
	}
	
	private void StartReceive() {
		
	}
	
	public void Send(String message) {
		
	}
}
