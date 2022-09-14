package io.github.sunshinewzy.sunnyflow.handler;

import io.github.sunshinewzy.sunnyflow.handler.impl.ChatHandler;
import io.github.sunshinewzy.sunnyflow.util.SunnyFlowUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class HandlerManager {
	private final Map<Integer, List<SunnyFlowHandler>> handlerMap = new ConcurrentHashMap<>();
	
	public void handle(Socket socket, DataInputStream in, DataOutputStream out) {
		try {
			handlerMap.clear();
			registerHandler(new ChatHandler(in, out));
			
			new Thread(() -> {
				try {
					while(!socket.isClosed()) {
						int id = in.readInt();
						List<SunnyFlowHandler> handlers = handlerMap.get(id);
						if(handlers != null) {
							for(SunnyFlowHandler handler : handlers) {
								handler.handle();
							}
						}

						Thread.sleep(100);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}).start();
		} catch (Exception ignored) {}
	}
	
	public void registerHandler(SunnyFlowHandler handler) {
		SunnyFlowUtil.putMapElement(handlerMap, handler.getId(), handler);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends SunnyFlowHandler> void consume(Class<T> clazz, Consumer<T> consumer) {
		handlerMap.forEach((id, list) -> {
			for(SunnyFlowHandler handler : list){
				if(clazz.isInstance(handler)) {
					consumer.accept((T) handler);
				}
			}
		});
	}

	
	public Map<Integer, List<SunnyFlowHandler>> getHandlerMap() {
		return handlerMap;
	}
}
