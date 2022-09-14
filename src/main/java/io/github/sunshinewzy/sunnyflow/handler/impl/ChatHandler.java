package io.github.sunshinewzy.sunnyflow.handler.impl;

import io.github.sunshinewzy.sunnyflow.handler.SunnyFlowHandler;
import io.github.sunshinewzy.sunnyflow.type.SunnyFlowType;
import org.bukkit.Bukkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatHandler extends SunnyFlowHandler {

	public ChatHandler(DataInputStream in, DataOutputStream out) {
		super(in, out);
	}


	@Override
	public int getId() {
		return SunnyFlowType.CLIENT_CHAT;
	}

	@Override
	public void handle() throws IOException {
		String text = in.readUTF();
		Bukkit.getConsoleSender().sendMessage(text);
	}

	public void chat(String text) {
		try {
			out.writeInt(SunnyFlowType.SERVER_CHAT);
			out.writeUTF(text);
		} catch (IOException ignore) {}
	}
	
}
