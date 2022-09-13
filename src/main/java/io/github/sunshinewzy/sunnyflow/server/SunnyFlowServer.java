package io.github.sunshinewzy.sunnyflow.server;

import io.github.sunshinewzy.sunnyflow.handler.HandlerManager;
import io.github.sunshinewzy.sunnyflow.util.SunnyFlowUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

public class SunnyFlowServer extends Thread {
	private final Logger logger;
	private final ServerSocket serverSocket;
	private final HandlerManager manager = new HandlerManager();


	public SunnyFlowServer(Logger logger, int port) throws IOException {
		this.logger = logger;
		this.serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000);
	}


	@Override
	public void run() {
		while(true) {
			Socket socket = new Socket();
//			logger.info("等待远程连接，端口号为：" + serverSocket.getLocalPort());
			try {
				socket = serverSocket.accept();
//				logger.info("远程主机地址：" + socket.getRemoteSocketAddress());
				
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				
				String password = in.readUTF();
				if(password.equals(SunnyFlowUtil.stringToMD5("password123456"))) {
					logger.info("成功与远程主机 (" + socket.getRemoteSocketAddress() + ") 建立连接");
					manager.handle(socket, in, out);
				} else {
					socket.close();
				}
			} catch (Exception exception) {
				try {
					socket.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}


	public HandlerManager getManager() {
		return manager;
	}
}
