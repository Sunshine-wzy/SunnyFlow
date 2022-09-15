package io.github.sunshinewzy.sunnyflow.server;

import io.github.sunshinewzy.sunnyflow.handler.SunnyFlowHandler;
import io.github.sunshinewzy.sunnyflow.handler.impl.ChatHandler;
import io.github.sunshinewzy.sunnyflow.packet.SunnyFlowPacket;
import io.github.sunshinewzy.sunnyflow.type.SunnyFlowType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SunnyFlowSession extends SunnyFlowThread {
	private final Socket client;
	private final String password;

	private final Map<Integer, SunnyFlowHandler> typeHandlerMap = new HashMap<>();
	private final Map<Class<?>, SunnyFlowHandler> classHandlerMap = new HashMap<>();
	private boolean authed = false;
	

	public SunnyFlowSession(Logger logger, Socket client, String password) throws IOException {
		super(logger, "SunnyFlow Client " + client.getRemoteSocketAddress());
		this.client = client;
		try {
			this.client.setSoTimeout(0);
		} catch (SocketException e) {
			running = false;
		}
		this.password = password;

		registerHandler(new ChatHandler(client));
	}

	
	@Override
	public void run() {
		int authFailureCount = 0;

		try {
			while(true) {
				if(running) {
					SunnyFlowPacket packet = SunnyFlowPacket.read(client.getInputStream());

					int type = packet.getType();
					if(type == SunnyFlowType.AUTHORIZE) {
						String text = packet.getText();
						if(!text.isEmpty() && text.contentEquals(password)) {
							authed = true;
							SunnyFlowPacket.write(client.getOutputStream(), SunnyFlowType.AUTHORIZE, packet.getRequestId(), "");
							continue;
						} else {
							authed = false;
							SunnyFlowPacket.write(client.getOutputStream(), -1, SunnyFlowType.AUTHORIZE, "");
						}
					}

					if(!authed) {
						authFailureCount++;
						if(authFailureCount >= 3) {
							return;
						}
						continue;
					}

					SunnyFlowHandler handler = typeHandlerMap.get(type);
					if(handler != null) {
						handler.handle(packet);
					}

					continue;
				}

				return;
			}
		} catch (IOException ignored) {
		} catch (Exception ex) {
			logger.warning("Exception whilst parsing SunnyFlow input: " + ex);
		} finally {
			closeSocket();
			logger.info("Thread " + name + " shutting down");
			running = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	public <T extends SunnyFlowHandler> T getHandler(Class<T> clazz) {
		SunnyFlowHandler handler = classHandlerMap.get(clazz);
		if(!clazz.isInstance(handler)) return null;
		return (T) handler;
	}
	
	

	private void closeSocket() {
		try {
			client.close();
		} catch (IOException ex) {
			logger.warning("Failed to close socket: " + ex);
		}
	}
	
	private void registerHandler(SunnyFlowHandler handler) {
		typeHandlerMap.put(handler.getType(), handler);
		classHandlerMap.put(handler.getClass(), handler);
	}
	
}
