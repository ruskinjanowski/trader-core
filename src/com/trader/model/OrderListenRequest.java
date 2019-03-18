package com.trader.model;

public class OrderListenRequest {

	public final String id;
	public final String type;

	public OrderListenRequest(String id, String type) {
		super();
		this.id = id;
		this.type = type;
	}
}
