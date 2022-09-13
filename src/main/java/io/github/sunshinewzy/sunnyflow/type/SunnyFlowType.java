package io.github.sunshinewzy.sunnyflow.type;

public enum SunnyFlowType {
	SERVER_CHAT(0),
	CLIENT_CHAT(1),
	
	;

	
	private final int id;
	
	SunnyFlowType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
