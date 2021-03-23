package com.trader.stats;

public class SdResult
{

	final double mean;
	final double sd;

	public SdResult(double mean, double sd) {
		super();
		this.mean = mean;

		this.sd = sd = Math.max(sd, 0.5);//put limit of 1

	}

	public double getMean() {
		return mean;
	}

	public double getSd() {
		return sd;
	}

	public double lowerLimit() {
		return mean - sd;
	}

	public double upperLimit() {
		return mean + sd;
	}

	public boolean isLowerLimit(double diff) {
		return diff < lowerLimit();
	}

	public boolean isUpperLimit(double diff) {
		return diff > upperLimit();
	}
	@Override
	public String toString() {
		return "SdResult [mean=" + mean + ", sd=" + sd + "]";
	}

}
