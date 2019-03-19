package com.trader.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Ensure that there are never more than n calls to rateLimit() per given period
 * by blocking the rateLimit() method for the required amount of time.
 *
 */
public class RateLimiter {

	List<Long> prevTimes = new ArrayList<>();

	int calls = 4;
	int period_ms = 5100;

	public synchronized void rateLimit() {

		long now = System.currentTimeMillis();

		prevTimes.add(now);

		if (prevTimes.size() < calls) {
			return;
		}
		List<Long> inPeriod = new ArrayList<>();

		long oldestInPeriod = now;
		for (Long l : prevTimes) {
			if (now - l <= period_ms) {
				inPeriod.add(l);
				if (l < oldestInPeriod) {
					oldestInPeriod = l;
				}
			}
		}
		prevTimes = inPeriod;

		if (prevTimes.size() < calls) {
			return;
		}

		long sleep = period_ms - (now - oldestInPeriod);
		System.out.println("sleeping luno:" + sleep);

		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException {
		RateLimiter rl = new RateLimiter();
		rl.rateLimit();

		rl.rateLimit();

		rl.rateLimit();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimit();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimit();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimit();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimit();
		rl.rateLimit();

		rl.rateLimit();
		rl.rateLimit();
		rl.rateLimit();
		rl.rateLimit();
		rl.rateLimit();
	}
}
