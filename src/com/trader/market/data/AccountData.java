package com.trader.market.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;

import com.trader.controller.api.Api;

import arbtrader.credentials.EMarketType;

/**
 * Amounts available on various Exchange accounts
 *
 */
public class AccountData {

	/** Static methods only */
	private AccountData() {
	}

	private static Map<AccountService, AccountInfo> infosMap = new HashMap<>();
	private static Map<AccountService, Long> updateMap = new HashMap<>();

	public static double getBalance(EMarketType market, Currency currency, double lag_m) {
		AccountService account = Api.getAccountService(market);

		Long prevTime = updateMap.get(account);
		if (prevTime == null || System.currentTimeMillis() - prevTime > lag_m * 60 * 1000) {
			updateMap.put(account, System.currentTimeMillis());
			try {
				infosMap.put(account, account.getAccountInfo());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		AccountInfo info = infosMap.get(account);
		for (Wallet w : info.getWallets().values()) {
			if (w.getBalances().containsKey(currency)) {
				double d = w.getBalance(currency).getAvailable().doubleValue();
				return d;
			}
		}
		throw new IllegalStateException();
	}

	public static double getLunoZAR(double lag) {
		return getBalance(EMarketType.ZAR_BTC, Currency.ZAR, lag);
	}

	public static double getLunoBTC(double lag) {
		return getBalance(EMarketType.ZAR_BTC, Currency.BTC, lag);
	}
	//
	// public static PendingTransaction getLunoIncomingBTC(double volume) {
	// updateIfNeeded(24 * 60, true, false);
	// for (Wallet w : lunoInfo.getWallets().values()) {
	// if (w.getBalance(Currency.BTC) != null) {
	// String accId = w.getId();
	// try {
	// LunoPendingTransactions pt = Api.lunoZARTrade().pendingTransactions(accId);
	// for (PendingTransaction t : pt.getTransactions()) {
	// if (Utility.isEqualVolume(t.available.doubleValue(), volume)) {
	// return t;
	// }
	// }
	// return null;
	//
	// } catch (LunoException | IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// throw new IllegalStateException();
	// }

	public static void main(String[] args) {

		getBalance(EMarketType.ZAR_BTC, Currency.USD, 1);

	}
}
