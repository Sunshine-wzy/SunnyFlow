package io.github.sunshinewzy.sunnyflow.handler;

import io.github.sunshinewzy.sunnyflow.packet.SunnyFlowPacket;

import java.net.Socket;

public abstract class SunnyFlowHandler {
	protected final Socket socket;

	
	protected SunnyFlowHandler(Socket socket) {
		this.socket = socket;
	}


	public abstract int getType();
	
	public abstract void handle(SunnyFlowPacket packet);
	
}
