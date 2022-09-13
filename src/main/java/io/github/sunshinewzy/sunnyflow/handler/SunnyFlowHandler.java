package io.github.sunshinewzy.sunnyflow.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class SunnyFlowHandler {
	protected final DataInputStream in;
	protected final DataOutputStream out;
	
	
	public SunnyFlowHandler(DataInputStream in, DataOutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	
	public abstract void handle();
	
}
