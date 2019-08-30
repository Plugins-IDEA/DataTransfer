package com.whimthen.intelliJ.transfer.tasks;

import com.whimthen.intelliJ.transfer.RunnableFunction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class ThreadContainer {

	private LinkedBlockingQueue<RunnableFunction> queue;

	private ThreadContainer() {
		ExecutorService service = Executors.newSingleThreadExecutor();
		this.queue = new LinkedBlockingQueue<>();
		service.submit(() -> {
			while (true) {
				try {
					RunnableFunction function = queue.take();
					function.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static class ThreadContainerHolder {
		private static final ThreadContainer INSTANCE = new ThreadContainer();
	}

	public static ThreadContainer getInstance() {
		return ThreadContainerHolder.INSTANCE;
	}

	public void exec(RunnableFunction function) {
		queue.offer(function);
	}

	public void log() {

	}

}
