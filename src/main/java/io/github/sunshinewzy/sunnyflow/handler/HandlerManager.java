package io.github.sunshinewzy.sunnyflow.handler;

import io.github.sunshinewzy.sunnyflow.handler.impl.ChatHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HandlerManager {
	private final List<SunnyFlowHandler> handlers = new ArrayList<>();
	
	public void handle(Socket socket, DataInputStream in, DataOutputStream out) {
		try {
			handlers.clear();
			registerHandler(new ChatHandler(in, out));
			
			new Thread(() -> {
				while(!socket.isClosed()) {
					try {
						for(SunnyFlowHandler handler : handlers) {
							handler.handle();
						}
						
						Thread.sleep(100);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
		} catch (Exception ignored) {}
	}
	
	public void registerHandler(SunnyFlowHandler handler) {
		handlers.add(handler);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends SunnyFlowHandler> void consume(Class<T> clazz, Consumer<T> consumer) {
		for(SunnyFlowHandler handler : handlers){
			if(clazz.isInstance(handler)) {
				consumer.accept((T) handler);
			}
		}
	}

	
	public List<SunnyFlowHandler> getHandlers() {
		return handlers;
	}
}
