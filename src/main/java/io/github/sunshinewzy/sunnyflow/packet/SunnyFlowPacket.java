package io.github.sunshinewzy.sunnyflow.packet;

import io.github.sunshinewzy.sunnyflow.packet.exception.MalformedPacketException;

import java.io.*;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class SunnyFlowPacket {
	
	private final int requestId;
	private final int type;
	private final byte[] payload;
	
	private SunnyFlowPacket(int requestId, int type, byte[] payload) {
		this.requestId = requestId;
		this.type = type;
		this.payload = payload;
	}
	
	public int getRequestId() {
		return requestId;
	}
	
	public int getType() {
		return type;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public String getText() {
		return new String(payload, StandardCharsets.UTF_8);
	}
	
	
	/**
	 * Send a SunnyFlow packet and fetch the response
	 * 
	 * @param connection SunnyFlowConnection instance
	 * @param type The packet type
	 * @param payload The payload (password, message, etc.)
	 * @return A SunnyFlowPacket object containing the response
	 */
	public static SunnyFlowPacket send(SunnyFlowConnection connection, int type, byte[] payload) throws IOException {
		try {
			SunnyFlowPacket.write(connection.getSocket().getOutputStream(), connection.getRequestId(), type, payload);
		} catch (SocketException se) {
			// Close the socket if something happens
			connection.getSocket().close();
			
			// Rethrow the exception
			throw se;
		}
		
		return SunnyFlowPacket.read(connection.getSocket().getInputStream());
	}
	
	/**
	 * Write a SunnyFlow packet on an outputstream
	 * 
	 * @param out The OutputStream to write on
	 * @param requestId The request id
	 * @param type The packet type
	 * @param payload The payload
	 */
	public static void write(OutputStream out, int requestId, int type, byte[] payload) throws IOException {
		int bodyLength = SunnyFlowPacket.getBodyLength(payload.length);
		int packetLength = SunnyFlowPacket.getPacketLength(bodyLength);
		
		ByteBuffer buffer = ByteBuffer.allocate(packetLength);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		buffer.putInt(bodyLength);
		buffer.putInt(requestId);
		buffer.putInt(type);
		buffer.put(payload);
		
		// Null bytes terminators
		buffer.put((byte)0);
		buffer.put((byte)0);
		
		// Woosh!
		out.write(buffer.array());
		out.flush();
	}

	public static void write(OutputStream out, int requestId, int type, String text) throws IOException {
		write(out, requestId, type, text.getBytes(StandardCharsets.UTF_8));
	}

	public static void write(OutputStream out, int type, byte[] payload) throws IOException {
		write(out, ThreadLocalRandom.current().nextInt(), type, payload);
	}

	public static void write(OutputStream out, int type, String text) throws IOException {
		write(out, ThreadLocalRandom.current().nextInt(), type, text);
	}
	
	/**
	 * Read an incoming SunnyFlow packet
	 * 
	 * @param in The InputStream to read on
	 * @return The read SunnyFlowPacket
	 */
	public static SunnyFlowPacket read(InputStream in) throws IOException {
		// Header is 3 4-bytes ints
		byte[] header = new byte[4 * 3];
		
		// Read the 3 ints
		if(in.read(header) < 10) throw new MalformedPacketException("The packet is too short (less than 10 bytes)");
		
		try {
			// Use a bytebuffer in little endian to read the first 3 ints
			ByteBuffer buffer = ByteBuffer.wrap(header);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			int length = buffer.getInt();
			int requestId = buffer.getInt();
			int type = buffer.getInt();
			
			// Payload size can be computed now that we have its length
			byte[] payload = new byte[length - 4 - 4 - 2];
			
			DataInputStream dis = new DataInputStream(in);
			
			// Read the full payload
			dis.readFully(payload);
			
			// Read the null bytes
			//noinspection ResultOfMethodCallIgnored
			dis.read(new byte[2]);
			
			return new SunnyFlowPacket(requestId, type, payload);
		} catch (BufferUnderflowException | EOFException ex) {
			throw new MalformedPacketException("Cannot read the whole packet");
		}
	}
	
	private static int getPacketLength(int bodyLength) {
		// 4 bytes for length + x bytes for body length
		return 4 + bodyLength;
	}
	
	private static int getBodyLength(int payloadLength) {
		// 4 bytes for requestId, 4 bytes for type, x bytes for payload, 2 bytes for two null bytes
		return 4 + 4 + payloadLength + 2;
	}

}
