package io.github.sunshinewzy.sunnyflow;

import io.github.sunshinewzy.sunnyflow.packet.SunnyFlowConnection;
import io.github.sunshinewzy.sunnyflow.packet.SunnyFlowPacket;
import io.github.sunshinewzy.sunnyflow.util.SunnyFlowUtil;

import java.io.IOException;
import java.util.Scanner;

public class ServerTest {
	private static final String hostname = "127.0.0.1";
	private static final int port = 25585;
	private static final String password = "awa123qwq";

	public static void main(String[] args) {
		connect();
	}
	
	
	public static void connect() {
		System.out.println("连接到主机：" + hostname + ":" + port);
		
		try {
			SunnyFlowConnection connection = new SunnyFlowConnection(hostname, port, SunnyFlowUtil.stringToMD5(password));

			new Thread(() -> {
				try {
					Scanner scanner = new Scanner(System.in);
					while(true) {
						String str = scanner.nextLine();
						connection.message(str);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}).start();

			int cnt = 0;
			while(true) {
				cnt++;
				System.out.println(cnt);
				SunnyFlowPacket packet = connection.read();
				String text = packet.getText();
				System.out.println("消息：" + text);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
