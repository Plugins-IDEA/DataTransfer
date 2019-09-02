package com.whimthen.intelliJ.transfer.tasks;

import com.whimthen.intelliJ.transfer.RunnableFunction;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class ThreadContainer {

	private LinkedBlockingQueue<RunnableFunction> queue;
	private ExecutorService service;

	private ThreadContainer() {
		this.queue = new LinkedBlockingQueue<>();
	}

	private ExecutorService getService() {
		if (Objects.isNull(service) || service.isShutdown()) {
			service = Executors.newSingleThreadExecutor();
		}
		return service;
	}

	public void shutdown() {
		if (Objects.nonNull(service)) {
			service.shutdown();
		}
	}

	public void run(Consumer<Throwable> consumer) {
		getService().submit(() -> {
			while (true) {
				try {
					RunnableFunction function = queue.take();
					function.run();
				} catch (Exception e) {
					consumer.accept(e);
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

	public void log(RunnableFunction function) {
		queue.offer(function);
	}

}
