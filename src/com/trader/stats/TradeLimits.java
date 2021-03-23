package com.trader.stats;

public class TradeLimits
{

	final double upper;
	final double lower;

	public TradeLimits(double upper, double lower) {

		this.upper = upper;
		this.lower = lower;
	}

	public double getUpper() {
		return upper;
	}

	public double getLower() {
		return lower;
	};

}
