package com.trader.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * Utility class which can be extended to load a .properties file.
 *
 */
public abstract class TradeProperties {

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

	protected boolean getPropertyB(String p) {
		String val = props.getProperty(p);
		if (val == null) {
			throw new IllegalStateException();
		}
		return Boolean.parseBoolean(val);
	}
}
