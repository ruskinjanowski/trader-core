package com.trader.controller.api;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitstamp.BitstampExchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.luno.LunoExchange;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

import arbtrader.credentials.AccountDetails;
import arbtrader.credentials.AccountDetails.LunoDetails;
import arbtrader.credentials.EMarketType;
import arbtrader.credentials.TraderFolders;
import arbtrader.credentials.TraderFolders.ProgramName;

public class Api {

	public static final CurrencyPair BTC_NGN = new CurrencyPair(Currency.BTC, Currency.NGN);
	// luno
	// public static final String PAIR_XBTZAR = "XBTZAR";
	// public static final int MIN_TRADE_ZAR = 100;
	// public static final double MIN_TRADE_BTC = 0.001;

	private static final Map<EMarketType, RateLimiter> mapLimiter = new HashMap<>();
	private static final Map<EMarketType, Exchange> mapExchanges = new HashMap<>();

	public static void createApis(ProgramName name) {
		File c = TraderFolders.getConfig(name);
		File p = new File(c, "accounts.properties");
		AccountDetails login = new AccountDetails(p);
		addLunoExchange(login.lunoZAR, EMarketType.ZAR_BTC);
		addLunoExchange(login.lunoEUR, EMarketType.EUR_BTC);
		addLunoExchange(login.lunoNGN, EMarketType.NGN_BTC);

		if (login.bitstamp != null) {
			ExchangeSpecification exSpec = new BitstampExchange().getDefaultExchangeSpecification();
			exSpec.setUserName(login.bitstamp.username);
			exSpec.setApiKey(login.bitstamp.key);
			exSpec.setSecretKey(login.bitstamp.secret);
			Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(exSpec);
			put(EMarketType.USD_BTC, bitstamp);
		}
	}

	private static void addLunoExchange(LunoDetails details, EMarketType market) {
		if (details != null) {
			ExchangeSpecification exSpec = new LunoExchange().getDefaultExchangeSpecification();// BitstampExchange().getDefaultExchangeSpecification();
			exSpec.setApiKey(details.key);
			exSpec.setSecretKey(details.secret);
			Exchange luno = ExchangeFactory.INSTANCE.createExchange(exSpec);
			put(market, luno);
		}
	}

	private static void put(EMarketType market, Exchange exchange) {
		mapLimiter.put(market, new RateLimiter());
		mapExchanges.put(market, exchange);
	}

	public static MarketDataService getMarketDataService(EMarketType marketType) {
		mapLimiter.get(marketType).rateLimitLuno();
		return mapExchanges.get(marketType).getMarketDataService();
	}

	public static TradeService getTradeService(EMarketType marketType) {
		mapLimiter.get(marketType).rateLimitLuno();
		return mapExchanges.get(marketType).getTradeService();
	}

	public static AccountService getAccountService(EMarketType marketType) {
		mapLimiter.get(marketType).rateLimitLuno();
		return mapExchanges.get(marketType).getAccountService();
	}

	public static BigDecimal format(double val) {

		return new BigDecimal(Math.abs(val)).setScale(6, RoundingMode.HALF_DOWN);
	}

}
