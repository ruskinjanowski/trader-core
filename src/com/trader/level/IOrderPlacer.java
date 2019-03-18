package com.trader.level;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.model.Order;

public interface IOrderPlacer {
	// enum CancelResult {
	// Success, Failure, DoesNotExist, AlreadyFilled
	// }

	Order placeOrder(double volume, OrderType type, double price);

	/**
	 * Request Luno to cancel the order.
	 * 
	 * @return true if the request was successful, false if not
	 */
	CancelResult cancelOrder(Order cancelling);
}
