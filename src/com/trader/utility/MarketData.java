package com.trader.utility;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.luno.dto.LunoException;
import org.openexchangerates.oerjava.CurrConv;
import org.openexchangerates.oerjava.exceptions.UnavailableExchangeRateException;

import com.trader.api.Api;
import com.trader.api.FiatCurrencies;
import com.trader.client.MarketEvents;
import com.trader.model.MarketType;
import com.trader.model.Spread;

/**
 * Current Exchange rates on various exchanges. Everything should be static as
 * there is every only one current state of the various exchanges.
 *
 */
public class MarketData {

	public static final MarketData INSTANCE = new MarketData();

	private MarketData() {

	}

	private MarketPrice ZARrUSD;
	private MarketPrice ZARrEUR;
	private MarketPrice ZARrNGN;
	/** Luno BTC price */
	private MarketPrice ZARrBTC;
	/** Bitstamp BTC price */
	private MarketPrice USDrBTC;
	/** Bitstamp BTC EUR price */
	private MarketPrice EURrBTC;

	private double lastDiff = -10000;

	/**
	 * @return difference between the forex ZARrUSD exchange rate and the ZARrUSD
	 *         exchange rate you would get if you traded on Bitstamp and Luno
	 * @throws IOException
	 * @throws LunoException
	 * @throws UnavailableExchangeRateException
	 */
	public synchronized double getDiff_perc(double lag_min) {

		getZARrUSD(lag_min);

		getZARrBTC(lag_min);

		getUSDrBTC(lag_min);

		double mZARrBTC = ZARrBTC.mid();
		double mUSDrBTC = USDrBTC.mid();
		double mZARrUSD = ZARrUSD.mid();

		double cryto_ZARrUSD = mZARrBTC / mUSDrBTC;
		double diff_perc = (cryto_ZARrUSD - mZARrUSD) / mZARrUSD * 100;

		// round
		BigDecimal bd = new BigDecimal(diff_perc);
		bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
		diff_perc = bd.doubleValue();

		if (lastDiff != diff_perc) {
			System.out.println("Diff updated: " + diff_perc);
			lastDiff = diff_perc;
		}

		return diff_perc;
	}

	public synchronized double getDiffEur_perc(double lag_min) {

		getZARrEUR(lag_min);

		getZARrBTC(lag_min);

		getEURrBTC(lag_min);

		double mZARrBTC = ZARrBTC.mid();
		double mEURrBTC = EURrBTC.mid();
		double mZARrEUR = ZARrEUR.mid();

		double cryto_ZARrEUR = mZARrBTC / mEURrBTC;
		double diff_perc = (cryto_ZARrEUR - mZARrEUR) / mZARrEUR * 100;

		// round
		BigDecimal bd = new BigDecimal(diff_perc);
		bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
		diff_perc = bd.doubleValue();

		if (lastDiff != diff_perc) {
			lastDiff = diff_perc;
		}

		return diff_perc;
	}

	public synchronized MarketPrice getZARrUSD(double lag_min) {
		if (ZARrUSD == null || ZARrUSD.olderThan(lag_min)) {
			double zu;
			try {
				zu = FiatCurrencies.ZARrUSD();
			} catch (UnavailableExchangeRateException e) {
				e.printStackTrace();
				return null;
			}
			ZARrUSD = new MarketPrice(zu, zu, new Date());
		}
		return ZARrUSD;
	}

	public synchronized MarketPrice getZARrEUR(double lag_min) {
		lag_min = Math.max(lag_min, 30);//don't overload api
		if (ZARrEUR == null || ZARrEUR.olderThan(lag_min)) {
			Double eurZar = CurrConv.getEurZar();
			if (eurZar != null) {
				ZARrEUR = new MarketPrice(eurZar, eurZar, new Date());
			}

		}
		return ZARrEUR;
	}

	public synchronized MarketPrice getZARrBTC(double lag_min) {
		Spread spread = MarketEvents.getSpread(MarketType.ZAR_BTC);
		if (spread != null) {
			ZARrBTC = new MarketPrice(spread.priceBid, spread.priceAsk, new Date());
		} else if (ZARrBTC == null || ZARrBTC.olderThan(lag_min)) {
			Ticker ticker;
			try {
				ticker = Api.getMarketDataService(MarketType.ZAR_BTC).getTicker(CurrencyPair.BTC_ZAR);
			} catch (IOException e) {
				e.printStackTrace();
				return ZARrBTC;

			}
			ZARrBTC = new MarketPrice(ticker.getBid().doubleValue(), ticker.getAsk().doubleValue(),
					ticker.getTimestamp());
		}
		return ZARrBTC;
	}

	public synchronized MarketPrice getUSDrBTC(double lag_min) {
		if (USDrBTC == null || USDrBTC.olderThan(lag_min)) {
			Ticker ticker;
			try {
				ticker = Api.getMarketDataService(MarketType.USD_BTC).getTicker(CurrencyPair.BTC_USD);
				USDrBTC = new MarketPrice(ticker.getBid().doubleValue(), ticker.getAsk().doubleValue(),
						ticker.getTimestamp());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return USDrBTC;
	}

	public synchronized MarketPrice getEURrBTC(double lag_min) {
		if (EURrBTC == null || EURrBTC.olderThan(lag_min)) {
			Ticker ticker;
			try {
				ticker = Api.getMarketDataService(MarketType.EUR_BTC)
						.getTicker(CurrencyPair.BTC_EUR);
				EURrBTC = new MarketPrice(ticker.getBid().doubleValue(), ticker.getAsk().doubleValue(),
						ticker.getTimestamp());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return EURrBTC;
	}

	public static class MarketPrice {
		public final double bid;
		public final double ask;

		public final Date marketTime_ms;
		public final long localTime_ms;

		public MarketPrice(double bid, double ask, Date marketTime_ms) {
			this.bid = bid;
			this.ask = ask;
			this.marketTime_ms = marketTime_ms;
			this.localTime_ms = System.currentTimeMillis();
		}

		public boolean olderThan(double age_min) {
			return System.currentTimeMillis() - localTime_ms > age_min * 60 * 1000 + 200;
		}

		public double mid() {
			return (bid + ask) / 2;
		}
	}
}
