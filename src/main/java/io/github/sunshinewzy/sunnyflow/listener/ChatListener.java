package io.github.sunshinewzy.sunnyflow.listener;

import io.github.sunshinewzy.sunnyflow.SunnyFlow;
import io.github.sunshinewzy.sunnyflow.handler.impl.ChatHandler;
import io.github.sunshinewzy.sunnyflow.server.SunnyFlowServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	private final SunnyFlowServer server = SunnyFlow.getSunnyFlowServer();
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String text = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
		server.consume(ChatHandler.class, (handler) -> handler.chat(text));
	}
	
}
