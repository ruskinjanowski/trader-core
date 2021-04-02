package com.trader.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trader.model.MarketType;
import com.trader.model.Events;
import com.trader.model.Order;
import com.trader.model.OrderCancelled;
import com.trader.model.Spread;

public class MarketEvents {

	private static final Map<MarketType, MarketEvents> me = new HashMap<>();
	static {
		createMarketEvents(MarketType.ZAR_BTC);
		createMarketEvents(MarketType.EUR_BTC);
		createMarketEvents(MarketType.NGN_BTC);
	}

	/**
	 * SpreadListeners which will get notified first.
	 */
	private final List<ISpreadListener> listenersSpreadHigh = new ArrayList<>();
	/**
	 * SpreadListeners which will get notified second.
	 */
	private final List<ISpreadListener> listenersSpreadLow = new ArrayList<>();
	private final List<IOrderListener> listenersOrder = new ArrayList<>();

	private Spread spread;

	final TaskQueue taskQueue = new TaskQueue();
	public final MarketType market;

	private static void createMarketEvents(MarketType market) {
		MarketEvents m = new MarketEvents(market);
		me.put(market, m);
	}

	private MarketEvents(MarketType market) {
		this.market = market;
	}

	/**
	 * HIGH priority will have its listener method called before those of LOW
	 * priority.
	 *
	 */
	public static enum ReceivePriority {
		LOW, HIGH
	}

	public static MarketEvents get(MarketType t) {
		return me.get(t);
	}

	public static Spread getSpread(MarketType t) {
		return me.get(t).spread;
	}

	public Spread getSpread() {
		return spread;
	}

	public void addSpreadListener(ISpreadListener l, ReceivePriority p) {
		if (p.equals(ReceivePriority.HIGH)) {
			listenersSpreadHigh.add(l);
		} else {
			listenersSpreadLow.add(l);
		}

	}

	public void addSpreadListener(ISpreadListener l) {
		addSpreadListener(l, ReceivePriority.LOW);
	}

	public void removeSpreadListener(ISpreadListener l) {
		listenersSpreadHigh.remove(l);
		listenersSpreadLow.remove(l);
	}

	public void addOrderListener(IOrderListener l) {
		listenersOrder.add(l);
	}

	public void removeOrderListener(IOrderListener l) {
		listenersOrder.remove(l);
	}

	Set<Spread> s = new HashSet<>();

	public void processEvents(Events e) {
		if (spread == null) {
			System.out.println(new Date() + " " + e);
		}

		if (e.spread != null) {
			// process spread changed events on a queue, this means some spread changed
			// events might be dropped. However if a batch of events come in the first and
			// last will always be processed.
			s.add(e.spread);
			spread = e.spread;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						s.remove(e.spread);
						//System.out.println("Skipped: " + s.size() + " current: " + e.spread);

						for (ISpreadListener l : listenersSpreadHigh) {
							l.spreadChanged();
						}
						for (ISpreadListener l : listenersSpreadLow) {
							l.spreadChanged();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			taskQueue.submitTask(runnable);

		}
		if (e.cancelled != null) {

			for (IOrderListener l : listenersOrder) {
				l.orderCancelled(e.cancelled);
			}
		}
		if (e.fullyFilled != null) {

			for (IOrderListener l : listenersOrder) {
				l.orderFilled(e.fullyFilled);
			}
		}
		if (e.placed != null) {
			for (IOrderListener l : listenersOrder) {
				l.orderPlaced(e.placed);
			}
		}
	}

	public interface ISpreadListener {
		/**
		 * The latest spread should be gotten by calling
		 * MarketEvents.getSpread(EMarketType.ZAR_BTC);
		 */
		void spreadChanged();

	}

	public interface IOrderListener {
		void orderPlaced(Order o);

		void orderFilled(Order o);

		void orderCancelled(OrderCancelled oc);
	}
}
