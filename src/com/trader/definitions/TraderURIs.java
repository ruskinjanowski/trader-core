package com.trader.definitions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.trader.model.MarketType;

public class TraderURIs {
	private static final Map<MarketType, Integer> marketPorts = new HashMap<>();
	static {
		marketPorts.put(MarketType.ZAR_BTC, 8025);
		marketPorts.put(MarketType.EUR_BTC, 8026);
		marketPorts.put(MarketType.NGN_BTC, 8027);
	}

	public static int getPortForMarket(MarketType market) {
		return marketPorts.get(market);
	}

	public static URI getURIForMarket(MarketType market) {
		try {
			return new URI("ws://localhost:" + getPortForMarket(market) + "/websockets/" + market.pair.base
					+ market.pair.counter + "/events");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
