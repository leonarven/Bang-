package network;

import java.nio.ByteBuffer;
import game.JSONObject;

public class JsonPacket extends PacketBase {
	protected JSONObject data;

	public JsonPacket(PacketType type, int id, JSONObject data) {		
		super(type, id);
		this.setData(data);
	}
	
	public JsonPacket( Packet packet ) throws Exception
		{ super(packet); } 

	public JSONObject getData() 
		{ return data; }
	
	protected void setData( ByteBuffer data )
		{ this.setData(new JSONObject(new String( data.array(), 6, data.limit() - 6 ))); }

	/*
	 * Asettaa datan dataksi
	 */
	public void setData( JSONObject data )
		{ this.data = data; }

	/*
	 * Muuttaa StringPacketin Packet:ksi (daa?)
	 */
	public Packet toPacket() {
		
		byte[] bytes = data.toString().getBytes();
		ByteBuffer buffer = ByteBuffer.allocate( Character.SIZE / 8 + Integer.SIZE / (8 * Integer.bitCount( super.getId() )) + bytes.length ); // FIXME depends on encoding?
		buffer.putChar( super.getType().toChar() );
		buffer.putInt( super.getId() );
		buffer.put( bytes ); // FIXME encodings.
		
		// Don't make copy of buffer
		return new Packet( buffer, false );	
	}
}