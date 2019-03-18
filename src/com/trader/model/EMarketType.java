package com.trader.model;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import com.trader.controller.api.Api;

public class EMarketType {

	public static final EMarketType ZAR_BTC = new EMarketType(ExchangeName.LUNO, CurrencyPair.BTC_ZAR);
	public static final EMarketType EUR_BTC = new EMarketType(ExchangeName.LUNO, CurrencyPair.BTC_EUR);
	public static final EMarketType NGN_BTC = new EMarketType(ExchangeName.LUNO, Api.BTC_NGN);
	public static final EMarketType USD_BTC = new EMarketType(ExchangeName.LUNO, CurrencyPair.BTC_USD);

	public enum ExchangeName {
		LUNO, BITSTAMP, FOREX
	}

	public final ExchangeName name;
	public final CurrencyPair pair;

	public EMarketType(ExchangeName name, CurrencyPair pair) {
		super();
		this.name = name;
		this.pair = pair;
	}

	public Currency getCounterCurrency() {
		return pair.counter;
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
		EMarketType other = (EMarketType) obj;
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
