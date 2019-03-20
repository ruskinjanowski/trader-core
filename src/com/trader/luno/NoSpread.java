package com.trader.luno;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.client.MarketEvents;
import com.trader.model.MarketType;
import com.trader.model.Spread;

public class NoSpread extends SpreadPricing {
	private final MarketType market;
	private final double minSpread;

	public NoSpread(MarketType market, double minSpread) {
		super();
		this.market = market;
		this.minSpread = minSpread;
	}

	@Override
	public double getPrice(OrderType type) {

		Spread spread = MarketEvents.getSpread(market);
		if (type.equals(OrderType.ASK)) {
			return spread.priceBid + minSpread;
		} else {
			return spread.priceAsk - minSpread;
		}
	}

	@Override
	public boolean priceCorrect(OrderType type, double price) {
		return getPrice(type) == price;
	}

}
