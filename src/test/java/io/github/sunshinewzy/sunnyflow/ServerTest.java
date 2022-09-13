package io.github.sunshinewzy.sunnyflow;

import io.github.sunshinewzy.sunnyflow.util.SunnyFlowUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerTest {
	private static final String hostname = "127.0.0.1";
	private static final int port = 25585;
	private static final String password = "password123456";

	public static void main(String[] args) {
		connect();
	}
	
	
	public static void connect() {
		System.out.println("连接到主机：" + hostname + ":" + port);
		try(Socket client = new Socket(hostname, port)) {
			System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			DataInputStream in = new DataInputStream(client.getInputStream());
			
			String md5 = SunnyFlowUtil.stringToMD5(password);
			System.out.println(md5);
			out.writeUTF(md5);
			
			while(true) {
				System.out.println("消息：" + in.readUTF());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
