package network;

import java.util.Date;
import java.util.Random;

public class Ping {
	private long	time;
	private long	startTime;
	private boolean	running;
	private long 	magicNumber;
	
	public Ping() {
		this.time		= -1;
		this.startTime	= -1;
		this.running	= false;
	}
	public void start() {
		this.magicNumber = (new Random()).nextLong();
		this.startTime = (new Date()).getTime();
		this.running = true;
	}
	public void stop() {
		this.time = (new Date()).getTime() - this.startTime;
		this.startTime = -1;
		this.running = false;
	}
	public boolean isRunning()
		{ return this.running; }
	public long getMagicNumber()
		{ return this.magicNumber; }
}
