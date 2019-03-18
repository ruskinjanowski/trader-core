package com.trader.luno;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.client.MarketEvents;
import com.trader.model.EMarketType;
import com.trader.model.SpreadChanged;

public class NoSpread extends SpreadPricing {
	private final EMarketType market;
	private final double minSpread;

	public NoSpread(EMarketType market, double minSpread) {
		super();
		this.market = market;
		this.minSpread = minSpread;
	}

	@Override
	public double getPrice(OrderType type) {

		SpreadChanged spread = MarketEvents.getSpread(market);
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
