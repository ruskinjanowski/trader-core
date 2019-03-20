package com.trader.model;

import org.knowm.xchange.currency.CurrencyPair;

import com.trader.api.Api;

public class MarketType {

	public static final MarketType ZAR_BTC = new MarketType(ExchangeName.LUNO, CurrencyPair.BTC_ZAR);
	public static final MarketType EUR_BTC = new MarketType(ExchangeName.LUNO, CurrencyPair.BTC_EUR);
	public static final MarketType NGN_BTC = new MarketType(ExchangeName.LUNO, Api.BTC_NGN);
	public static final MarketType USD_BTC = new MarketType(ExchangeName.BITSTAMP, CurrencyPair.BTC_USD);

	public enum ExchangeName {
		LUNO, BITSTAMP, FOREX
	}

	public final ExchangeName name;
	public final CurrencyPair pair;

	public MarketType(ExchangeName name, CurrencyPair pair) {
		super();
		this.name = name;
		this.pair = pair;
	}

	@Override
	public String toString() {
		return name + " " + pair;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pair == null) ? 0 : pair.hashCode());
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
		MarketType other = (MarketType) obj;
		if (name != other.name)
			return false;
		if (pair == null) {
			if (other.pair != null)
				return false;
		} else if (!pair.equals(other.pair))
			return false;
		return true;
	}

}
