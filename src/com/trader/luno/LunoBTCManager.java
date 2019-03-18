package com.trader.luno;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knowm.xchange.dto.Order.OrderType;

import com.trader.client.MarketEvents;
import com.trader.client.MarketEvents.IOrderListener;
import com.trader.client.MarketEvents.ISpreadListener;
import com.trader.model.EMarketType;
import com.trader.model.Order;
import com.trader.model.OrderCancelled;
import com.trader.utility.Utility;

public class LunoBTCManager implements IOrderListener, ISpreadListener {

	private final EMarketType market;
	private final SpreadPricing pricing;

	final AccWallet wallet;

	double wantedBTC;

	private final LunoOrderPlacer placer;

	/**
	 * Order id with tracker ob
	 */
	final Map<String, OrderTracker> ids = new HashMap<>();

	private final List<IOrderFilled> listeners = new ArrayList<>();

	public LunoBTCManager(EMarketType market, AccWallet wallet) {
		super();
		this.market = market;
		this.wallet = wallet;
		this.wantedBTC = wallet.getBtc();
		pricing = SomeSpread.getDefaultForMarket(market);
		placer = new LunoOrderPlacer(market);
		MarketEvents.get(market).addOrderListener(this);
		MarketEvents.get(market).addSpreadListener(this);
		new TimeoutPoller().start();
	}

	public synchronized void setWantedBTC(double wanted) {
		if (wanted == wantedBTC) {
			return;
		}
		System.out.println("wanted btc new: " + wanted + " old: " + wantedBTC);
		wantedBTC = wanted;

		// cancel all open orders

		cancelAllOrders();
		// new orders will be placed once cancel is confirmed
		placeOrderIfNeeded();
	}

	public void tradeBTC(double volume) {
		setWantedBTC(wallet.getBtc() + volume);
	}

	public double getWantedBTC() {
		return wantedBTC;
	}

	public AccWallet getWallet() {
		return wallet;
	}

	@Override
	public synchronized void spreadChanged() {

		for (OrderTracker t : ids.values()) {
			if (t.orderState.equals(OrderState.PLACE_CONFIRMED)) {
				if (!pricing.priceCorrect(t.orderType, t.o.price)) {
					cancelOrder(t);
				}
			}

		}
	}

	private synchronized void placeOrderIfNeeded() {
		if (ids.isEmpty()) {
			if (!Utility.isEqualVolume(wantedBTC, wallet.getBtc())) {
				double vv = wantedBTC - wallet.getBtc();
				OrderType type = Utility.getType(vv);

				double volume = Math.abs(vv);
				double price = pricing.getPrice(type);

				if (!wallet.canPlace(volume, type, price)) {
					throw new IllegalStateException();
				}

				String sTime = LunoBTCManager.getMilliTime();
				// System.out.println(sTime + " Placing: " + o + " " + type);

				Order o = placer.placeOrder(volume, type, price);
				OrderTracker t = new OrderTracker(o, type);
				t.setOrderState(OrderState.PLACING);
				ids.put(o.id, t);
				wallet.orderPlaced(t);
			}
		} else {
			System.out.println("orders open: " + ids);
		}

		// if (om.getOrderState().equals(OrderState.PLACE_CONFIRMED)) {
		//
		// System.out.println("order pending...");
		// } else {
		// if (System.currentTimeMillis() - om.stateTime > 5_000) {
		//
		// om.cancelOrder();
		//
		// }
		// }
	}

	public void addOrderFilledListener(IOrderFilled l) {
		listeners.add(l);
	}

	/**
	 * The only stable states are PlaceConfirmed and Complete. All other states are
	 * 'busy processing' states, or the error state.
	 *
	 */
	enum OrderState {
		INITIAL, PLACING, PLACE_CONFIRMED, CANCELLING,
		/** Order cancel confirmed or order filled */
		COMPLETE, ERROR
	}

	public void cancelAllOrders() {
		for (OrderTracker t : ids.values()) {
			cancelOrder(t);
		}
	}

	public synchronized void cancelOrder(OrderTracker t) {
		if (!t.orderState.equals(OrderState.PLACE_CONFIRMED) && !t.isTimedOut()) {
			System.out.println("Not cancelling due to incorrect state..." + t.orderState);
			return;
		}
		System.out.println(getMilliTime() + " Cancelling: " + t.o);
		com.trader.luno.CancelResult res = null;
		try {
			res = placer.cancelOrder(t.o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(res);
		switch (res.orderStatus) {
		case CANCELLING:
			t.setOrderState(OrderState.CANCELLING);
			break;
		case COMPLETE:
			orderComplete(t, res.o.volume);
			break;
		case ERROR:
			t.setOrderState(OrderState.ERROR);
			break;
		}
		System.out.println("dfdfdf: " + t.orderState);
	}

	private synchronized void orderComplete(OrderTracker t, double fill) {
		t.orderComplete(fill);

		wallet.orderComplete(t);
		ids.remove(t.o.id);
		placeOrderIfNeeded();

		if (t.getFill() != 0) {
			// notify listeners
			for (IOrderFilled l : listeners) {
				l.orderFilled(t);
			}
		}
	}

	@Override
	public synchronized void orderFilled(Order o) {
		if (ids.containsKey(o.id)) {
			OrderTracker t = ids.get(o.id);
			orderComplete(t, o.volume);
		}
	}

	@Override
	public synchronized void orderCancelled(OrderCancelled oc) {
		if (ids.containsKey(oc.order.id)) {
			System.out.println(getMilliTime() + " Cancel confirmed: " + oc);
			OrderTracker t = ids.get(oc.order.id);
			orderComplete(t, oc.fill);
		}
	}

	@Override
	public synchronized void orderPlaced(Order o) {

		if (ids.containsKey(o.id)) {
			System.out.println(getMilliTime() + " Order confirmed: " + o);
			OrderTracker t = ids.get(o.id);
			t.setOrderState(OrderState.PLACE_CONFIRMED);
			if (!pricing.priceCorrect(t.orderType, o.price)) {
				cancelOrder(t);
			}

		}
	}

	public static String getMilliTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		return sdf.format(d);
	}

	class TimeoutPoller extends Thread {

		public TimeoutPoller() {
			super("Timeout Polling");
		}

		@Override
		public void run() {
			while (true) {

				for (OrderTracker t : ids.values()) {
					if (t.isTimedOut()) {
						System.out.println("--------> Order time out " + t.o);
						cancelOrder(t);
					}

				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
