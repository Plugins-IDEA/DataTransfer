package com.whimthen.intelliJ.transfer.tasks;

import com.whimthen.intelliJ.transfer.utils.RunnableFunction;

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

	private        LinkedBlockingQueue<RunnableFunction> queue;
	private        ExecutorService                       service;
	private static ThreadContainer                       threadContainer;

	private ThreadContainer(Consumer<Throwable> consumer) {
		this.queue = new LinkedBlockingQueue<>();
		this.run(consumer);
	}

	private ExecutorService getService() {
		if (Objects.isNull(service) || service.isShutdown()) {
			this.service = Executors.newSingleThreadExecutor();
		}
		return this.service;
	}

	public void shutdown() {
		if (Objects.nonNull(this.service)) {
			this.service.shutdown();
		}
	}

	private void run(Consumer<Throwable> consumer) {
		getService().submit(() -> {
			while (true) {
				try {
					RunnableFunction function = this.queue.take();
					function.run();
				} catch (Exception e) {
					consumer.accept(e);
				}
			}
		});
	}

	public static ThreadContainer getInstance(Consumer<Throwable> consumer) {
		if (Objects.isNull(threadContainer)) {
			synchronized (ThreadContainer.class) {
				if (Objects.isNull(threadContainer)) {
					threadContainer = new ThreadContainer(consumer);
				}
			}
		}
		return threadContainer;
	}

	public void add(RunnableFunction function) {
		this.queue.offer(function);
	}

}
