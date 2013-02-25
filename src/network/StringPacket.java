package network;

import java.nio.ByteBuffer;

public class StringPacket extends PacketBase {
	protected String data;

	public StringPacket( PacketType type, int id, String data ) {		
		super(type, id);
		this.setData(data);
	}

	public StringPacket( Packet packet ) throws Exception
		{ super(packet); } 

	public String getData() 
		{ return data; }
	
	protected void setData( ByteBuffer data )
		{ this.setData(new String( data.array(), 6, data.limit() - 6 )); }

	/*
	 * Asettaa datan dataksi
	 */
	public void setData( String data )
		{ this.data = data; }

	/*
	 * Muuttaa StringPacketin Packet:ksi (daa?)
	 */
	public Packet toPacket() {
		
		byte[] bytes = data.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( 6 + bytes.length ); // FIXME depends on encoding?

		buffer.putChar( super.getType().toChar() );
		buffer.putInt( super.getId() );
		buffer.put( bytes ); // FIXME encodings.

/*		System.out.println("pos: "+buffer.position());
		System.out.println("lenC: "+java.lang.Character.SIZE / 8);
		System.out.println("lenI: "+Integer.SIZE / (8 * Integer.bitCount(super.getId())));
		System.out.println("lenB: "+bytes.length);
		System.out.println("len: "+(java.lang.Character.SIZE / 8 + Integer.SIZE / (8 * Integer.bitCount(super.getId())) + bytes.length));*/
		
		// Don't make copy of buffer
		return new Packet( buffer, false );	
	}
}