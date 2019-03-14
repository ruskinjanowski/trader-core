package arbtrader.model;

public class OrderCancelled {

	/**
	 * The order that got cancelled
	 */
	public final Order order;
	/**
	 * Volume filled of this order
	 */
	public final double fill;

	public OrderCancelled(Order order, double fill) {
		super();
		this.order = order;
		this.fill = fill;
	}

	@Override
	public String toString() {
		return "OrderCancelled [order=" + order + ", fill=" + fill + "]";
	}

}
