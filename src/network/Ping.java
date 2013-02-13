/*package network;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Random;

public class Ping {
	private long	time;
	private long	startTime;
	private boolean	running;
	private long 	magicNumber;
	
	public Ping() {
		this.magicNumber= -1;
		this.time		= -1;
		this.startTime	= -1;
		this.running	= false;
	}
	public Packet start(int from, int to) {
		this.magicNumber = (new Random()).nextLong();

		Packet packet = new Packet(PacketType.PING, from, to, ByteBuffer.allocate(8).putLong(magicNumber));
		
		this.startTime = (new Date()).getTime();
		this.running = true;

		return packet;
	}
	public Packet start() 
		{ return this.start(0, 0); }

	public void stop() {
		this.time = (new Date()).getTime() - this.startTime;
		this.startTime = -1;
		this.running = false;
	}
	public boolean validate(Packet packet) { 
		return (packet.getLong(0) == this.magicNumber); 
	}

	public boolean isRunning()
		{ return this.running; }
	public long getMagicNumber()
		{ return this.magicNumber; }
	public long getTime()
		{ return this.time; }
}*/
