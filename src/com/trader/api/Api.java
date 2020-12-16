package com.trader.api;

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

import com.trader.api.AccountDetails.BitstampDetails;
import com.trader.api.AccountDetails.LunoDetails;
import com.trader.definitions.TraderFolders;
import com.trader.definitions.TraderFolders.ProgramName;
import com.trader.model.MarketType;

public class Api {

	public static final CurrencyPair BTC_NGN = new CurrencyPair(Currency.BTC, Currency.NGN);

	private static final Map<MarketType, RateLimiter> mapLimiter = new HashMap<>();
	private static final Map<MarketType, Exchange> mapExchanges = new HashMap<>();

	public static void createApis(ProgramName name) {
		File c = TraderFolders.getConfig(name);
		File p = new File(c, "accounts.properties");
		AccountDetails login = new AccountDetails(p);
		addLunoExchange(login.lunoZAR, MarketType.ZAR_BTC);
		addLunoExchange(login.lunoEUR, MarketType.EUR_BTC);
		addLunoExchange(login.lunoNGN, MarketType.NGN_BTC);

		if (login.bitstamp != null) {
			ExchangeSpecification exSpec = new BitstampExchange().getDefaultExchangeSpecification();
			exSpec.setUserName(login.bitstamp.username);
			exSpec.setApiKey(login.bitstamp.key);
			exSpec.setSecretKey(login.bitstamp.secret);
			Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(exSpec);
			put(MarketType.USD_BTC, bitstamp);
		}
	}

	public static void addLunoExchange(LunoDetails details, MarketType market) {
		if (details != null) {
			ExchangeSpecification exSpec = new LunoExchange().getDefaultExchangeSpecification();// BitstampExchange().getDefaultExchangeSpecification();
			exSpec.setApiKey(details.key);
			exSpec.setSecretKey(details.secret);
			Exchange luno = ExchangeFactory.INSTANCE.createExchange(exSpec);
			put(market, luno);
		}
	}

	public static void addBitstampExchange(BitstampDetails details) {
		ExchangeSpecification exSpec = new BitstampExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(details.username);
		exSpec.setApiKey(details.key);
		exSpec.setSecretKey(details.secret);
		Exchange bitstamp = ExchangeFactory.INSTANCE.createExchange(exSpec);
		put(MarketType.USD_BTC, bitstamp);
	}

	private static void put(MarketType market, Exchange exchange) {
		mapLimiter.put(market, new RateLimiter());
		mapExchanges.put(market, exchange);
	}

	public static MarketDataService getMarketDataService(MarketType marketType) {
		mapLimiter.get(marketType).rateLimit();
		return mapExchanges.get(marketType).getMarketDataService();
	}

	public static TradeService getTradeService(MarketType marketType) {
		mapLimiter.get(marketType).rateLimit();
		return mapExchanges.get(marketType).getTradeService();
	}

	public static AccountService getAccountService(MarketType marketType) {
		mapLimiter.get(marketType).rateLimit();
		return mapExchanges.get(marketType).getAccountService();
	}

	public static BigDecimal format(double val) {

		return new BigDecimal(Math.abs(val)).setScale(6, RoundingMode.HALF_DOWN);
	}

}
