package com.trader.api;

import org.openexchangerates.oerjava.OpenExchangeRates;
import org.openexchangerates.oerjava.exceptions.UnavailableExchangeRateException;

import com.trader.model.EMarketType;

/**
 * Class to expose fiat currencies API.
 * 
 * Update call only once every 2 hours as there is a 1000/month call limit.
 * 
 * Everything should be made static as there is only ever one state of fiat
 * currencies.
 * 
 * @author ruskin
 *
 */
public class FiatCurrencies {
	private final static OpenExchangeRates oer = new OpenExchangeRates("6d4443a00a5c47bf8d4d7b8f2dab5e8e");

	/** ZAR/USD updated not more than once per 2 hours */
	private static double ZAR;
	static long ZAR_time = 0;

	/** ZAR/USD updated not more than once per 2 hours */
	private static double NGN;
	static long NGN_time = 0;

	/**
	 * This method will return an updated value every 2 hours.
	 * 
	 * @return ZAR/USD exchange rate
	 * @throws UnavailableExchangeRateException
	 */
	public static synchronized double ZARrUSD() throws UnavailableExchangeRateException {
		if (System.currentTimeMillis() - ZAR_time > 2 * 60 * 60 * 1000) {
			ZAR = oer.currency("ZAR").doubleValue();
			ZAR_time = System.currentTimeMillis();
		}
		return ZAR;
	}

	public static synchronized double NGNrUSD() throws UnavailableExchangeRateException {
		if (System.currentTimeMillis() - NGN_time > 2 * 60 * 60 * 1000) {
			NGN = oer.currency("NGN").doubleValue();
			NGN_time = System.currentTimeMillis();
		}
		return NGN;
	}

	// public static final CurrencyPair ZAR_NGN = new CurrencyPair(Currency.NGN,
	// Currency.ZAR);
	public static double getZARrX(EMarketType X) {
		if (X.equals(EMarketType.NGN_BTC)) {
			try {
				return ZARrUSD() / NGNrUSD();
			} catch (UnavailableExchangeRateException e) {

				e.printStackTrace();
				return 0;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

}
