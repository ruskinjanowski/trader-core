package com.trader.utility;

import java.text.DecimalFormat;

import org.knowm.xchange.dto.Order.OrderType;

public class Utility {

	private static final DecimalFormat d6 = new DecimalFormat("#.######");

	/**
	 * Round to 6 decimal places. Bitstamp requires to have no more than 8 decimal
	 * places.
	 * 
	 * @return Round to 6 decimal places
	 */
	public static String round(double val) {
		String rounded = d6.format(val);
		if (!rounded.equals(Double.toString(val))) {
			System.out.println("Rounded: " + val + " to: " + rounded);
		}
		return rounded;
	}

	/**
	 * Checks if a volume is equal to within acceptable decimal precision
	 * 
	 */
	public static boolean isEqualVolume(double v1, double v2) {
		boolean eq = Math.abs(v1 - v2) < 0.000001;
		if (!eq) {
			System.out.println("Volumes not equal s1: " + v1 + " s2: " + v2);
		}
		return eq;
	}

	public static OrderType getType(double volume) {

		if (volume > 0) {
			return OrderType.BID;
		} else if (volume < 0) {
			return OrderType.ASK;
		} else {
			return null;
		}
	}

	public static double volumeVector(double volume, OrderType type) {
		return type.equals(OrderType.BID) ? volume : -volume;
	}

}
