package com.trader.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;

import com.trader.luno.OrderTracker;

public class Transaction {

	public static final DateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.ENGLISH);

	public final Date date;
	public final String id;
	public final double volume;
	public final double price;
	public final OrderType type;
	public final CurrencyPair pair;

	public Transaction(Date date, String id, double volume, double price, OrderType type, CurrencyPair pair) {
		this.date = date;
		this.id = id;
		this.volume = volume;
		this.price = price;
		this.type = type;
		this.pair = pair;
	}

	public Transaction(String id, double volume, double price, OrderType type, CurrencyPair pair) {
		this(new Date(), id, volume, price, type, pair);
	}

	@Override
	public String toString() {
		return FORMAT.format(date) + "," + id + "," + volume + "," + price + "," + type + "," + pair;
	}

	public static Transaction fromString(String s) {
		String[] vals = s.split(",");
		Date date = null;
		try {
			date = FORMAT.parse(vals[0]);
		} catch (ParseException e) {
			// e.printStackTrace();
		}

		String id = vals[1];
		double volume = Double.parseDouble(vals[2]);
		double price = Double.parseDouble(vals[3]);
		OrderType type = OrderType.valueOf(vals[4]);
		CurrencyPair pair = new CurrencyPair(vals[5]);
		return new Transaction(date, id, volume, price, type, pair);
	}

	public static List<Transaction> fromFile(File f) {
		try (Scanner sc = new Scanner(f)) {
			List<Transaction> lines = new ArrayList<>();
			while (sc.hasNextLine()) {
				lines.add(fromString(sc.nextLine()));
			}
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Transaction fromTracker(OrderTracker t, CurrencyPair pair) {
		return new Transaction(t.o.id, t.getFill(), t.o.price, t.orderType, pair);
	}

	public static void main(String[] args) {
		Transaction to = new Transaction("dsaas", 0.11, 50002, OrderType.ASK, CurrencyPair.BTC_ZAR);
		String s = to.toString();
		Transaction t2 = fromString(s);
		System.out.println(t2);

	}
}
