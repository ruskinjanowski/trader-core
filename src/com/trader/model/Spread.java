package com.trader.model;

public class Spread {

	/** ASK price */
	public final double priceAsk;
	/** BID price */
	public final double priceBid;

	public Spread(double priceAsk, double priceBid) {
		this.priceAsk = priceAsk;
		this.priceBid = priceBid;
		if (priceAsk <= priceBid) {
			throw new IllegalStateException();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(priceAsk);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(priceBid);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Spread other = (Spread) obj;
		if (Double.doubleToLongBits(priceAsk) != Double.doubleToLongBits(other.priceAsk))
			return false;
		if (Double.doubleToLongBits(priceBid) != Double.doubleToLongBits(other.priceBid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SpreadChanged [priceAsk=" + priceAsk + ", priceBid=" + priceBid + "]";
	}

}
