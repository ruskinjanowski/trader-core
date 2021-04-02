package com.trader.luno;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.luno.service.LunoTradeService;

import com.trader.api.Api;
import com.trader.client.EventClientEndpoint;
import com.trader.luno.CancelResult.CancelStatus;
import com.trader.model.MarketType;
import com.trader.model.Order;
import com.trader.model.OrderListenRequest;

public class LunoOrderPlacer implements IOrderPlacer {
	private final MarketType market;

	public LunoOrderPlacer(MarketType market) {
		this.market = market;
	}

	@Override
	public CancelResult cancelOrder(Order cancelling) {
		try {
			LunoTradeService tradeService = (LunoTradeService) Api.getTradeService(market);
			tradeService.cancelOrder(cancelling.id);

		} catch (Exception le) {
			if (le.getMessage().contains("Cannot stop unknown or non-pending order")) {
				System.out.println("todo: Cannot stop unknown or non-pending order " + cancelling);

				// check state of order via luno api
				Collection<org.knowm.xchange.dto.Order> orders = null;
				try {
					LunoTradeService tradeService = (LunoTradeService) Api.getTradeService(market);
					orders = tradeService.getOrder(cancelling.id);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(orders);

				if (orders.iterator().hasNext()) {
					org.knowm.xchange.dto.Order o = orders.iterator().next();
					System.out.println("njfndfjd: " + o);
					Order filledOrder = new Order(cancelling.id, cancelling.price,
							o.getCumulativeAmount().doubleValue());
					CancelResult res = new CancelResult(filledOrder, CancelStatus.COMPLETE);
					return res;
				} else {
					// unexpected
					le.printStackTrace();
					System.out.println("debog point");
					return new CancelResult(null, CancelStatus.ERROR);
				}
			} else {
				// unexpected
				le.printStackTrace();
				return new CancelResult(null, CancelStatus.ERROR);
			}
		}
		return new CancelResult(null, CancelStatus.CANCELLING);
	}

	@Override
	public Order placeOrder(double volume, OrderType type, double price) {
		volume = round(volume);
		LimitOrder lo = new LimitOrder(type, Api.format(volume), market.pair, "123456733", new Date(),
				Api.format(price));
		String sTime = LunoBTCManager.getMilliTime();
		System.out.println(sTime + " Placing: " + lo);
		String orderid;
		try {
			LunoTradeService tradeService = (LunoTradeService) Api.getTradeService(market);
			orderid = tradeService.placeLimitOrder(lo);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		System.out.println("Placed id: " + orderid);

		Order placed = new Order(orderid, price, Api.format(volume).doubleValue());
		EventClientEndpoint.sendListenRequest(new OrderListenRequest(orderid, type.toString()), market);
		return placed;
	}

	private static double round(double d) {
		double n = 1e4;
		return Math.round(d * n) / n;
	}

	public static void main(String[] args) {
		System.out.println(Api.format(0.015390));
	}
}
