package network;

import java.nio.ByteBuffer;

public class IntPacket extends PacketBase {
	protected int data;

	public IntPacket(PacketType type, int id, int data) {		
		super(type, id);
		this.setData(data);
	}
	
	public IntPacket( Packet packet ) throws Exception
		{ super(packet); } 

	public int getData() 
		{ return data; }
	
	protected void setData( ByteBuffer data )
		{ this.setData(data.getInt()); }

	/*
	 * Asettaa datan dataksi
	 */
	public void setData( int data )
		{ this.data = data; }

	/*
	 * Muuttaa StringPacketin Packet:ksi (daa?)
	 */
	public Packet toPacket() {

		ByteBuffer buffer = ByteBuffer.allocate( java.lang.Character.SIZE/8 + 2 * Integer.SIZE / (8 * Integer.bitCount(super.getId())) ); // FIXME depends on encoding?
		buffer.putChar( super.getType().toChar() );
		buffer.putInt( super.getId() );
		buffer.putInt( data );

		// Don't make copy of buffer
		return new Packet( buffer, false );	
	}
}