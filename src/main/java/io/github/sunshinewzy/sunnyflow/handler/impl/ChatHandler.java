package io.github.sunshinewzy.sunnyflow.handler.impl;

import io.github.sunshinewzy.sunnyflow.handler.SunnyFlowHandler;
import io.github.sunshinewzy.sunnyflow.packet.SunnyFlowPacket;
import io.github.sunshinewzy.sunnyflow.type.SunnyFlowType;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.Socket;

public class ChatHandler extends SunnyFlowHandler {

	public ChatHandler(Socket socket) {
		super(socket);
	}
	

	@Override
	public int getType() {
		return SunnyFlowType.CLIENT_CHAT;
	}

	@Override
	public void handle(SunnyFlowPacket packet) {
		Bukkit.broadcastMessage(packet.getText());
	}

	
	public void chat(String text) {
		try {
			SunnyFlowPacket.write(socket.getOutputStream(), SunnyFlowType.SERVER_CHAT, text);
		} catch (IOException ignore) {}
	}
	
}
