package com.trader.stats;

import java.util.Collection;
import java.util.TreeMap;

public class StatsHistory
{

	/**
	 * Minimum history to calculate sd over.
	 */
	final double minHistory = 0.5;
	/**
	 * Default history to calculate sd over.
	 */
	final double historyDurationDays = 1;
	/**
	 * Data older than this will not be saved.
	 */
	final double maxHistory = 2;

	/**
	 * Timestamp, Price
	 */
	TreeMap<Long, Double> prices = new TreeMap<>();

	public boolean hasHistory() {
		return prices.firstKey() < System.currentTimeMillis() - minHistory * 24 * 60 * 60 * 1000;
	}

	public void addPoint(double price) {
		while(!prices.isEmpty()&&prices.firstKey()<System.currentTimeMillis() - maxHistory * 24 * 60 * 60 * 1000) {
			prices.remove(prices.firstKey());
		}
		prices.put(System.currentTimeMillis(), price);
		lastResult = null;
	}

	private SdResult lastResult = null;
	public  SdResult calculateSd() {
		if (lastResult != null) {
			return lastResult;
		}
		SdResult sd = calculateStandardDeviation(prices.values());
		lastResult = sd;
		System.out.println("Sd updated: " + sd);
		return sd;
	 }

	//math

	static SdResult calculateStandardDeviation(Collection<Double> data) {

		double sum = 0;
		double newSum = 0;

		for (Double d : data) {
			sum = sum + d;
		}
		double mean = (sum) / (data.size());

		for (Double d : data) {
			// put the calculation right in there
			newSum = newSum + ((d - mean) * (d - mean));
		}
		double squaredDiffMean = (newSum) / (data.size());
		double standardDev = (Math.sqrt(squaredDiffMean));

		return new SdResult(mean, standardDev);
	}
}
