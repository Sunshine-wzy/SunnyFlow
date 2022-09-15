package io.github.sunshinewzy.sunnyflow.server;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public abstract class SunnyFlowThread implements Runnable {
	private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
	
	protected volatile boolean running;
	protected final Logger logger;
	protected final String name;
	@Nullable
	protected Thread thread;


	public SunnyFlowThread(Logger logger, String name) {
		this.logger = logger;
		this.name = name;
	}
	

	public synchronized boolean start() {
		if(running) return true;
		
		running = true;
		thread = new Thread(this, name + " #" + UNIQUE_THREAD_ID.incrementAndGet());
		thread.start();
		
		return true;
	}
	
	public synchronized void stop() {
		running = false;
		
		if(thread != null) {
			int i = 0;
			
			while(thread.isAlive()) {
				try {
					thread.join(1000L);
					i++;
					if(i >= 5) {
						logger.warning("Waited " + i + " seconds attempting force stop!");
					} else if(thread.isAlive()) {
						logger.warning("Thread " + this + " (" + thread.getState() + ") failed to exit after " + i + " seconds");
						thread.interrupt();
					}
				} catch (InterruptedException ignored) {}
			}
			
			logger.info("Thread " + name + " stopped");
			thread = null;
		}
	}

	
	public boolean isRunning() {
		return running;
	}
}
