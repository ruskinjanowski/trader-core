package com.trader.model;

/**
 * An event of interest on the Luno market.
 *
 */
public class Events {

	/**
	 * The spread changed, this is the new spread.
	 */
	public final Spread spread;
	/**
	 * An order of interest got fully filled.
	 */
	public final Order fullyFilled;
	/**
	 * An order of interest got cancelled.
	 */
	public final OrderCancelled cancelled;
	/**
	 * An order of interest got placed.
	 */
	public final Order placed;

	public Events(Spread spread, Order fullyFilled, OrderCancelled cancelled, Order placed) {
		super();
		this.spread = spread;
		this.fullyFilled = fullyFilled;
		this.cancelled = cancelled;
		this.placed = placed;
	}

	@Override
	public String toString() {
		return "Events [spread=" + spread + ", fullyFilled=" + fullyFilled + ", cancelled=" + cancelled + ", placed="
				+ placed + "]";
	}

}
