package io.github.sunshinewzy.sunnyflow.server;

import io.github.sunshinewzy.sunnyflow.handler.SunnyFlowHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class SunnyFlowServer extends SunnyFlowThread {
	private final String password;
	
	private final ServerSocket serverSocket;
	private final List<SunnyFlowSession> clients = new ArrayList<>();
	

	public SunnyFlowServer(Logger logger, int port, String password) throws IOException {
		super(logger, "SunnyFlow Server");
		this.serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
		this.password = password;
	}


	@Override
	public void run() {
		try {
			while(running) {
				try {
					Socket client = serverSocket.accept();
					SunnyFlowSession session = new SunnyFlowSession(logger, client, password);
					session.start();
					
					clients.add(session);
					clearClients();
				} catch (SocketTimeoutException ex) {
					clearClients();
				} catch (IOException ex) {
					if(running) {
						logger.info("IO exception: " + ex);
					}
				}
			}
		} finally {
			closeSocket();
		}
	}
	
	@Override
	public void stop() {
		running = false;
		closeSocket();
		super.stop();

		for(SunnyFlowSession client : clients){
			if(client.isRunning()) {
				client.stop();
			}
		}
		
		clients.clear();
	}

	public <T extends SunnyFlowHandler> void consume(Class<T> clazz, Consumer<T> consumer) {
		clients.forEach(client -> {
			T handler = client.getHandler(clazz);
			if(handler != null) {
				consumer.accept(handler);
			}
		});
	}
	
	
	private void clearClients() {
		clients.removeIf(client -> !client.isRunning());
	}
	
	private void closeSocket() {
		try {
			serverSocket.close();
		} catch (IOException ex) {
			logger.warning("Failed to close socket: " + ex);
		}
	}
	
}
