package io.github.sunshinewzy.sunnyflow.handler.impl;

import io.github.sunshinewzy.sunnyflow.handler.SunnyFlowHandler;
import io.github.sunshinewzy.sunnyflow.type.SunnyFlowType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatHandler extends SunnyFlowHandler {

	public ChatHandler(DataInputStream in, DataOutputStream out) {
		super(in, out);
	}

	
	@Override
	public void handle() {
		
	}

	public void chat(String text) {
		try {
			out.writeInt(SunnyFlowType.SERVER_CHAT.getId());
			out.writeUTF(text);
		} catch (IOException ignore) {}
	}
	
}
