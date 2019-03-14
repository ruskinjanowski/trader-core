package com.trader.market.data;

/**
 * Local cache of executed trades. Caches from creation of this class.
 */
public class TradeData {

	// private static long lastUpdate = System.currentTimeMillis();
	// /** All trades executed since java started running */
	// private static final Set<UserTrade> lunoTrades = new HashSet<>();
	//
	// private static void updateTradedVolume_BTC(double lag_min) throws Exception {
	//
	// if (System.currentTimeMillis() - lastUpdate < lag_min * 60 * 1000) {
	// return;
	// }
	//
	// System.out.println(new Date() + " luno trades updated");
	//
	// LunoUserTrades lus = Api.luno().listTrades("XBTZAR", lastUpdate - 5000,
	// null);
	// UserTrade[] trades = lus.getTrades();
	//
	// lunoTrades.addAll(Arrays.asList(trades));
	//
	// lastUpdate = System.currentTimeMillis();
	// }
	//
	// private static CompletedTrades lunoVolume(Set<String> orders, double lag_min,
	// TradeRequest tr) throws Exception {
	// updateTradedVolume_BTC(lag_min);
	// double tradeVolume_BTC = 0;
	// double tradeVolume_ZAR = 0;
	// for (UserTrade trade : lunoTrades) {
	// if (orders.contains(trade.orderId)) {
	// tradeVolume_BTC = tradeVolume_BTC + trade.volume.doubleValue();
	// tradeVolume_ZAR = tradeVolume_ZAR + trade.price.doubleValue() *
	// trade.volume.doubleValue();
	// }
	// }
	// double exchangeRate = tradeVolume_ZAR / tradeVolume_BTC;
	//
	// CompletedTrades ct = new CompletedTrades(orders, tradeVolume_BTC,
	// exchangeRate, tr.type, tr.description);
	// return ct;
	// }
	//
	// public static CompletedTrades getVolume(TradeRequest tr, Set<String> orders)
	// throws Exception {
	// if (tr.type.equals(ConversionType.BTCtoZAR) ||
	// tr.type.equals(ConversionType.ZARtoBTC)) {
	// return lunoVolume(orders, 1, tr);
	// } else {
	// throw new IllegalArgumentException();
	// }
	// }
}
