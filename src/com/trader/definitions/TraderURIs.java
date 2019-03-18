package com.trader.definitions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.trader.model.EMarketType;

public class TraderURIs {
	private static final Map<EMarketType, Integer> marketPorts = new HashMap<>();
	static {
		marketPorts.put(EMarketType.ZAR_BTC, 8025);
		marketPorts.put(EMarketType.EUR_BTC, 8026);
		marketPorts.put(EMarketType.NGN_BTC, 8027);
	}

	public static int getPortForMarket(EMarketType market) {
		return marketPorts.get(market);
	}

	public static URI getURIForMarket(EMarketType market) {
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
