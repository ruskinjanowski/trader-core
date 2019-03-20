package com.trader.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.service.account.AccountService;

import com.trader.api.Api;
import com.trader.model.MarketType;

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

	public static double getBalance(MarketType market, Currency currency, double lag_m) {
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
		return getBalance(MarketType.ZAR_BTC, Currency.ZAR, lag);
	}

	public static double getLunoBTC(double lag) {
		return getBalance(MarketType.ZAR_BTC, Currency.BTC, lag);
	}
}
