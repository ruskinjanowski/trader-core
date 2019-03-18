package com.trader.luno;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.client.MarketEvents;
import com.trader.model.EMarketType;
import com.trader.model.SpreadChanged;

public class SomeSpread extends SpreadPricing {

	EMarketType market;
	public final double inc;
	public final double minSpread;

	public SomeSpread(EMarketType market, double inc, double minSpread) {
		super();
		this.market = market;
		this.inc = inc;
		this.minSpread = minSpread;
	}

	@Override
	public double getPrice(OrderType type) {
		SpreadChanged spread = MarketEvents.getSpread(market);

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
		SpreadChanged spread = MarketEvents.getSpread(market);
		boolean b = price <= spread.priceAsk && price >= spread.priceBid;
		return b;
	}

	public static SomeSpread getDefaultForMarket(EMarketType market) {

		if (market.equals(EMarketType.ZAR_BTC)) {
			return new SomeSpread(market, 3, 1);
		} else if (market.equals(EMarketType.EUR_BTC)) {
			return new SomeSpread(market, 0.1, 0.01);
		} else if (market.equals(EMarketType.NGN_BTC)) {
			return new SomeSpread(market, 10, 1);
		} else {
			throw new IllegalArgumentException();
		}

	}

}
