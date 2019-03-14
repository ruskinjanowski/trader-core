package arbtrader.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaskQueue {

	private final ExecutorService executorService = Executors.newFixedThreadPool(1);

	Future<?> prevTask = null;

	public synchronized void submitTask(Runnable runnableTask) {
		if (prevTask != null) {
			prevTask.cancel(false);
		}
		prevTask = executorService.submit(runnableTask);
	}

	public static void main(String[] args) {

		Runnable runnableTask1 = () -> {
			try {
				System.out.println("task started1");
				TimeUnit.MILLISECONDS.sleep(3000);
				System.out.println("task stopped1");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		Runnable runnableTask3 = () -> {
			try {
				System.out.println("task started3");
				TimeUnit.MILLISECONDS.sleep(3000);
				System.out.println("task stopped3");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		Runnable runnableTask2 = () -> {
			try {
				System.out.println("task started2");
				TimeUnit.MILLISECONDS.sleep(3000);
				System.out.println("task stopped2");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		TaskQueue tq = new TaskQueue();
		tq.submitTask(runnableTask1);
		tq.submitTask(runnableTask2);
		tq.submitTask(runnableTask3);
	}
}
