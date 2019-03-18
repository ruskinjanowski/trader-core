package com.trader.level;

import com.trader.model.Order;

public class CancelResult {

	public enum CancelStatus {
		CANCELLING, COMPLETE, ERROR
	}

	/**
	 * Order with volume which got filled.
	 */
	public final Order o;
	public final CancelStatus orderStatus;

	public CancelResult(Order o, CancelStatus orderStatus) {
		this.o = o;
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString() {
		return "CancelResult [o=" + o + ", orderStatus=" + orderStatus + "]";
	}

}
