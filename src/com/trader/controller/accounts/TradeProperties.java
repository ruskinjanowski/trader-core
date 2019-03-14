package com.trader.controller.accounts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class TradeProperties {

	// public static final TradeProperties INSTANCE = new TradeProperties(new
	// File("trading.properties"));

	protected final Properties props = new Properties();

	public TradeProperties(File f) {
		System.out.println("Loading properties file: " + f.getAbsolutePath());

		try {
			props.load(new FileInputStream(f));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected double getProperty(String p) {
		String val = props.getProperty(p);
		if (val == null) {
			throw new IllegalStateException();
		}
		return Double.parseDouble(val);
	}
}
