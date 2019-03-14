package com.trader.controller.api;

import java.util.ArrayList;
import java.util.List;

public class RateLimiter {

	List<Long> prevTimes = new ArrayList<>();

	int calls = 4;
	int period_ms = 5100;

	int cal = 0;

	public synchronized void rateLimitLuno() {
		cal++;

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
		System.out.println(cal + " " + prevTimes.size());

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
		rl.rateLimitLuno();

		rl.rateLimitLuno();

		rl.rateLimitLuno();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimitLuno();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimitLuno();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimitLuno();
		System.out.println("sleep 1s");
		Thread.sleep(1000);
		rl.rateLimitLuno();
		rl.rateLimitLuno();

		rl.rateLimitLuno();
		rl.rateLimitLuno();
		rl.rateLimitLuno();
		rl.rateLimitLuno();
		rl.rateLimitLuno();
	}
}
