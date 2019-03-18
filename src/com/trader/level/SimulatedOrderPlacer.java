package com.trader.level;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.controller.api.Api;
import com.trader.model.Order;

public class SimulatedOrderPlacer implements IOrderPlacer {
	public final List<Order> placed = new ArrayList<>();

	@Override
	public Order placeOrder(double volume, OrderType type, double price) {
		Order o = new Order(new Double(Math.random()).toString(), price, Api.format(volume).doubleValue());
		placed.add(o);
		return o;
	}

	@Override
	public CancelResult cancelOrder(Order cancelling) {
		return null;

	}

}
