package com.trader.single;

import java.util.HashMap;
import java.util.Map;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.Order.OrderType;

import com.trader.market.data.AccountData;
import com.trader.utility.Utility;

import arbtrader.credentials.EMarketType;

public class AccWallet {

	private double btc;
	private double counter;

	private double availableBTC;
	private double avaibleCounter;

	/**
	 * open orders
	 */
	Map<String, OrderTracker> orders = new HashMap<>();

	// public AccWallet(double btc, double counter) {
	// super();
	// this.btc = btc;
	// this.counter = counter;
	// }

	public AccWallet(EMarketType market) {
		btc = Math.floor(AccountData.getBalance(market, Currency.BTC, 1) * 1000) / 1000;
		counter = Math.floor(AccountData.getBalance(market, market.getCounterCurrency(), 1));
		avaibleCounter = counter;
		availableBTC = btc;
		System.out
				.println("Volumes: " + Currency.BTC + ": " + btc + " " + market.getCounterCurrency() + ": " + counter);

	}

	/**
	 * 
	 * @param o
	 *            order with the volume that got filled
	 * @param type
	 */
	public synchronized void orderComplete(OrderTracker t) {
		if (!orders.containsKey(t.o.id)) {
			throw new IllegalStateException();
		}
		double vv = Utility.volumeVector(t.getFill(), t.orderType);
		btc += vv;
		counter += (-vv) * t.o.price;
		if (t.o.volume != 0) {
			System.out.println("new wallet balance: " + btc + " , " + counter);
		}
		orders.remove(t.o.id);
		updateAvailableBalance();
	}

	public synchronized void orderPlaced(OrderTracker t) {
		orders.put(t.o.id, t);
		updateAvailableBalance();
	}

	boolean canPlace(double volume, OrderType type, double price) {
		if (type.equals(OrderType.BID)) {

			return avaibleCounter >= volume * price;
		} else if (type.equals(OrderType.ASK)) {
			return availableBTC >= volume;
		} else {
			throw new IllegalStateException();
		}
	}

	public double getBtc() {
		return roundBTC(btc);
	}

	public double getCounter() {
		return counter;
	}

	public double getAvailableCounter() {
		updateAvailableBalance();
		return roundBTC(avaibleCounter);
	}

	public double getAvailableBTC() {
		updateAvailableBalance();
		return roundBTC(availableBTC);
	}

	public double getMaxBuy(double price) {
		return AccWallet.roundBTC(avaibleCounter / price);
	}

	public static double roundBTC(double btc) {
		return Math.floor(btc * 10000) / 10000;
	}

	void updateAvailableBalance() {
		double usedBTC = 0;
		double usedCounter = 0;

		for (OrderTracker t : orders.values()) {
			if (t.orderType.equals(OrderType.BID)) {

				usedCounter += t.o.volume * t.o.price;
			} else if (t.orderType.equals(OrderType.ASK)) {
				usedBTC += t.o.volume;
			} else {
				throw new IllegalStateException();
			}
		}
		double naCounter = counter - usedCounter;
		double naBTC = btc - usedBTC;
		if (naCounter != avaibleCounter || naBTC != btc) {
			System.out.println(
					"New available: " + naBTC + "," + naCounter + " Prev: " + availableBTC + "," + avaibleCounter);
		}

		avaibleCounter = naCounter;
		availableBTC = naBTC;
	}
}
