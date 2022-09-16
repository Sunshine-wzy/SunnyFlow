package io.github.sunshinewzy.sunnyflow.packet;

import io.github.sunshinewzy.sunnyflow.packet.exception.AuthenticationException;
import io.github.sunshinewzy.sunnyflow.type.SunnyFlowType;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class SunnyFlowConnection {
	
	private final Object sync = new Object();
	private final Random rand = new Random();
	
	private int requestId;
	private Socket socket;
	
	private Charset charset = StandardCharsets.UTF_8;
	private final String host;
	private final int port;
	private final byte[] password;

	/**
	 * Create, connect and authenticate a new SunnyFlowConnection object
	 * 
	 * @param host SunnyFlow server address
	 * @param port SunnyFlow server port
	 * @param password SunnyFlow server password
	 *
	 */
	public SunnyFlowConnection(String host, int port, byte[] password) throws IOException, AuthenticationException {
		this.host = host;
		this.port = port;
		this.password = password;

		// Connect to host
		connect();
	}
	
	public SunnyFlowConnection(String host, int port, String password) throws IOException, AuthenticationException {
		this(host, port, password.getBytes(StandardCharsets.UTF_8));
	}
	
	
	/**
	 * Connect to a SunnyFlow server
	 */
	public void connect() throws IOException, AuthenticationException {
		if(host == null || host.trim().isEmpty()) {
			throw new IllegalArgumentException("Host can't be null or empty");
		}
		
		if(port < 1 || port > 65535) {
			throw new IllegalArgumentException("Port is out of range");
		}
		
		// Connect to the SunnyFlow server
		synchronized(sync) {
			// New random request id
			this.requestId = rand.nextInt();
			
			// We can't reuse a socket, so we need a new one
			this.socket = new Socket(host, port);
		}
		
		// Send the auth packet
		SunnyFlowPacket res = this.send(SunnyFlowType.AUTHORIZE, password);
		
		// Auth failed
		if(res.getRequestId() == -1) {
			throw new AuthenticationException("Password rejected by server");
		}
	}
	
	/**
	 * Disconnect from the current server
	 */
	public void disconnect() throws IOException {
		synchronized(sync) {
			this.socket.close();
		}
	}
	
	/**
	 * Send a message to the server
	 * 
	 * @param text The command to send
	 */
	public void message(String text) throws IOException {
		if(text == null || text.trim().isEmpty()) {
			throw new IllegalArgumentException("Text can't be null or empty");
		}
		
		write(SunnyFlowType.CLIENT_CHAT, text);
	}
	
	
	public SunnyFlowPacket send(int type, byte[] payload) throws IOException {
		synchronized(sync) {
			return SunnyFlowPacket.send(this, type, payload);
		}
	}
	
	public void write(int requestId, int type, byte[] payload) throws IOException {
		SunnyFlowPacket.write(socket.getOutputStream(), requestId, type, payload);
	}

	public void write(int requestId, int type, String text) throws IOException {
		SunnyFlowPacket.write(socket.getOutputStream(), requestId, type, text);
	}

	public void write(int type, byte[] payload) throws IOException {
		SunnyFlowPacket.write(socket.getOutputStream(), requestId, type, payload);
	}

	public void write(int type, String text) throws IOException {
		SunnyFlowPacket.write(socket.getOutputStream(), requestId, type, text);
	}
	
	public SunnyFlowPacket read() throws IOException {
		return SunnyFlowPacket.read(socket.getInputStream());
	}
	

	public int getRequestId() {
		return requestId;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public Charset getCharset() {
		return charset;
	}
	
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public byte[] getPassword() {
		return password;
	}
}
