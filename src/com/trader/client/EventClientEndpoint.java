package com.trader.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.Gson;
import com.trader.definitions.TraderURIs;
import com.trader.model.EMarketType;
import com.trader.model.Events;
import com.trader.model.OrderListenRequest;

@ClientEndpoint
public class EventClientEndpoint {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static final Gson gson = new Gson();

	private static final Map<EMarketType, Set<Session>> sessionMap = new HashMap<>();

	static {
		sessionMap.put(EMarketType.ZAR_BTC, new HashSet<>());
		sessionMap.put(EMarketType.EUR_BTC, new HashSet<>());
		sessionMap.put(EMarketType.NGN_BTC, new HashSet<>());
	}

	/**
	 * When a new instance of this class gets made, what this is at that time will
	 * get saved and events passed on to that.
	 */
	private static EMarketType proxy = EMarketType.ZAR_BTC;

	final MarketEvents eventsHandler = MarketEvents.get(proxy);

	private Set<Session> getSessions() {
		return sessionMap.get(eventsHandler.market);
	}

	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected ... " + session.getId());

		getSessions().add(session);
	}

	@OnMessage
	public String onMessage(String message, Session session) {
		if ("ka".equals(message)) {
			return "client ka";
		}

		Events ev = gson.fromJson(message, Events.class);
		eventsHandler.processEvents(ev);

		return null;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
		getSessions().remove(session);
	}

	/**
	 * Request the event server to receive events for an order.
	 * 
	 * @param olr the order to receive events for
	 * @param market the market on which the order was placed
	 */
	public static void sendListenRequest(OrderListenRequest olr, EMarketType market) {
		Set<Session> sessions = sessionMap.get(market);
		for (Session session : sessions) {
			try {
				session.getBasicRemote().sendText(gson.toJson(olr));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void startClient() {
		startClient(EMarketType.ZAR_BTC);
	}

	public static void startClient(EMarketType market) {

		proxy = market;

		ClientManager client = ClientManager.createClient();
		try {
			client.connectToServer(EventClientEndpoint.class, TraderURIs.getURIForMarket(market));

		} catch (DeploymentException e) {
			throw new RuntimeException(e);
		}
	}
}
