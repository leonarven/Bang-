package network;

import java.util.Date;
import java.util.Random;

public class Ping {
	private long	time;
	private long	startTime;
	private boolean	running;
	private byte[] 	magicNumber;
	
	public Ping() {
		this.magicNumber = new byte[4];
		this.time		= -1;
		this.startTime	= -1;
		this.running	= false;
	}
	public Packet start(int from, int to) {
		(new Random()).nextBytes(this.magicNumber);

		Packet packet = new Packet('P', from, to, this.magicNumber);
		
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
	public boolean validate(Packet packet)
		{ return (packet.getData() == this.magicNumber); }

	public boolean isRunning()
		{ return this.running; }
	public byte[] getMagicNumber()
		{ return this.magicNumber; }
	public long getTime()
		{ return this.time; }
}
