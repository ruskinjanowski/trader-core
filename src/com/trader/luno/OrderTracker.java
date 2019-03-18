package com.trader.luno;

import java.util.UUID;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.luno.LunoBTCManager.OrderState;
import com.trader.model.Order;

public class OrderTracker {

	public final long guid = UUID.randomUUID().getLeastSignificantBits();
	private long updateTime;
	OrderState orderState;
	public final Order o;
	public final OrderType orderType;
	/**
	 * Amount the order was filled when complete.
	 */
	private Double fill = null;

	public OrderTracker(Order o, OrderType orderType) {
		this.o = o;
		this.orderType = orderType;
		setOrderState(OrderState.INITIAL);
	}

	public void orderComplete(double fill) {
		this.fill = fill;
		setOrderState(OrderState.COMPLETE);
	}

	public OrderState getStatus() {
		return orderState;
	}

	public void setStatus(OrderState status) {
		this.orderState = status;
	}

	void setOrderState(OrderState orderState) {
		this.orderState = orderState;
		updateTime = System.currentTimeMillis();
	}

	public Double getFill() {
		return fill;
	}

	public boolean isTimedOut() {

		if (orderState.equals(OrderState.PLACING) || orderState.equals(OrderState.CANCELLING)) {
			return System.currentTimeMillis() - updateTime > 5_000;
		} else {
			return false;
		}
	}
}
