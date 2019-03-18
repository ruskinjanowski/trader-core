package com.trader.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 
 * If Runnables A, B and C are submitted in that order and C is submitted while
 * A is still executing, then B will be discarded and never executed while C
 * will start execution as soon as A is complete.
 *
 */
public class TaskQueue {

	private final ExecutorService executorService = Executors.newFixedThreadPool(1);

	Future<?> prevTask = null;

	public synchronized void submitTask(Runnable runnableTask) {
		if (prevTask != null) {
			prevTask.cancel(false);
		}
		prevTask = executorService.submit(runnableTask);
	}
}
