package com.trader.luno;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.client.MarketEvents;
import com.trader.model.MarketType;
import com.trader.model.Spread;

public class SomeSpread extends SpreadPricing {

	MarketType market;
	public final double inc;
	public final double minSpread;

	public SomeSpread(MarketType market, double inc, double minSpread) {
		super();
		this.market = market;
		this.inc = inc;
		this.minSpread = minSpread;
	}

	@Override
	public double getPrice(OrderType type) {
		Spread spread = MarketEvents.getSpread(market);

		if (spread.priceAsk - spread.priceBid > 2 * inc) {
			if (type.equals(OrderType.ASK)) {
				return spread.priceAsk - inc;
			} else {
				return spread.priceBid + inc;
			}
		} else {
			// no spread
			if (type.equals(OrderType.ASK)) {
				return spread.priceBid + minSpread;
			} else {
				return spread.priceAsk - minSpread;
			}
		}
	}

	@Override
	public boolean priceCorrect(OrderType type, double price) {
		Spread spread = MarketEvents.getSpread(market);
		boolean b = price <= spread.priceAsk && price >= spread.priceBid;
		return b;
	}

	public static SomeSpread getDefaultForMarket(MarketType market) {

		if (market.equals(MarketType.ZAR_BTC)) {
			return new SomeSpread(market, 3, 1);
		} else if (market.equals(MarketType.EUR_BTC)) {
			return new SomeSpread(market, 5, 0.01);
		} else if (market.equals(MarketType.NGN_BTC)) {
			return new SomeSpread(market, 10, 1);
		} else {
			throw new IllegalArgumentException();
		}

	}

}
