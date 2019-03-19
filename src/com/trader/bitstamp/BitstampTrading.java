package com.trader.bitstamp;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;

import com.trader.api.Api;
import com.trader.definitions.TraderFolders.ProgramName;
import com.trader.model.EMarketType;
import com.trader.utility.Utility;

public class BitstampTrading {

	static double totVol = 0;

	public static Order placeOrder(double volume) {
		return placeOrder(volume, OrderType.BID);
	}

	public static Order placeOrder(double volume, OrderType type) {

		totVol += volume;
		if (totVol > 2) {
			throw new IllegalStateException();
		}
		MarketOrder marketOrder = new MarketOrder(type, new BigDecimal(Utility.round(volume)), CurrencyPair.BTC_USD);

		String id;
		try {
			id = Api.getTradeService(EMarketType.USD_BTC).placeMarketOrder(marketOrder);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("id: " + id);

		System.out.println("Placed bitstamp order: " + id + " volume: " + volume);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		try {
			Collection<org.knowm.xchange.dto.Order> order = Api.getTradeService(EMarketType.USD_BTC).getOrder(id);
			System.out.println(order);
			return order.iterator().next();
		} catch (IOException e) {
			e.printStackTrace();
			return null;

		}

	}

	public static void sendToLuno(double volume_BTC) {
		String btcReceiveAdd = "181tasGykzVSARD56Jkn2h5TTm25LVSsdL";
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		BigDecimal bd = new BigDecimal(volume_BTC).setScale(7, RoundingMode.HALF_DOWN);
		try {

			String res = Api.getAccountService(EMarketType.USD_BTC).withdrawFunds(Currency.BTC, bd, btcReceiveAdd);
			System.out.println("Bitstamp withdraw funds: " + res + " volume BTC: " + volume_BTC);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// placeOrder(0.237);

		Api.createApis(ProgramName.BasicArbTrader);
		sendToLuno(0.63223);
	}
}
