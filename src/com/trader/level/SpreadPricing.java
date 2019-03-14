package com.trader.level;

import org.knowm.xchange.dto.Order.OrderType;

/**
 * Calculate price based on spread.
 *
 */
public abstract class SpreadPricing {

	public abstract double getPrice(OrderType type);

	public abstract boolean priceCorrect(OrderType type, double price);

	public double getPrice(double volume) {
		OrderType type = volume > 0 ? OrderType.BID : OrderType.ASK;
		return getPrice(type);
	}
}
