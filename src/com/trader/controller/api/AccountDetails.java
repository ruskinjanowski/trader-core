package com.trader.controller.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.trader.model.EMarketType;

public class AccountDetails {

	public final BitstampDetails bitstamp;
	public final LunoDetails lunoZAR;
	public final LunoDetails lunoEUR;
	public final LunoDetails lunoNGN;

	private final Properties props = new Properties();

	public void loadAccountDetails(String name) {

	}

	/**
	 * 
	 * @param f
	 *            accounts.properties file
	 */
	public AccountDetails(File f) {
		// File f = new File(TraderFolders.CONFIG, "accounts.properties");
		System.out.println("Loading properties file: " + f.getAbsolutePath());

		try {
			props.load(new FileInputStream(f));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (props.getProperty("lunoKey") != null) {
			throw new IllegalStateException("bad props");
		}

		if (props.getProperty("lunoZARKey") != null) {
			System.out.println("Using LunoZAR");
			lunoZAR = new LunoDetails(getProperty("lunoZARKey"), getProperty("lunoZARSecret"),
					getProperty("lunoZARBitcoinReceiveAddress"));
		} else {
			lunoZAR = null;
		}

		if (props.getProperty("lunoEURKey") != null) {
			System.out.println("Using LunoEUR");
			lunoEUR = new LunoDetails(getProperty("lunoEURKey"), getProperty("lunoEURSecret"),
					getProperty("lunoEURBitcoinReceiveAddress"));
		} else {
			lunoEUR = null;
		}

		if (props.getProperty("lunoNGNKey") != null) {
			System.out.println("Using LunoNGN");
			lunoNGN = new LunoDetails(getProperty("lunoNGNKey"), getProperty("lunoNGNSecret"),
					getProperty("lunoNGNBitcoinReceiveAddress"));
		} else {
			lunoNGN = null;
		}

		if (props.getProperty("bitstampUsername") != null) {
			System.out.println("Using Bitstamp");
			bitstamp = new BitstampDetails(getProperty("bitstampUsername"), getProperty("bitstampKey"),
					getProperty("bitstampSecret"));
		} else {
			bitstamp = null;
		}
	}

	private String getProperty(String p) {
		String val = props.getProperty(p);
		if (val == null) {
			System.out.println("bhjbdjsvdjskjjs");
			throw new IllegalStateException();
		}
		return val;
	}

	public static class BitstampDetails {
		public final String username;
		public final String key;
		public final String secret;

		public BitstampDetails(String username, String key, String secret) {
			this.username = username;
			this.key = key;
			this.secret = secret;
		}
	}

	public static class LunoDetails {
		public final String key;
		public final String secret;
		private final String bitcoinReceiveAddress;

		public LunoDetails(String key, String secret, String bitcoinReceiveAddress) {
			super();
			this.key = key;
			this.secret = secret;
			this.bitcoinReceiveAddress = bitcoinReceiveAddress;
		}

		public String getBitcoinReceiveAddress() {
			return bitcoinReceiveAddress;
		}

	}

	public LunoDetails getLunoDetails(EMarketType market) {
		if (market.equals(EMarketType.ZAR_BTC)) {
			return lunoZAR;
		} else if (market.equals(EMarketType.EUR_BTC)) {
			return lunoEUR;
		} else if (market.equals(EMarketType.NGN_BTC)) {
			return lunoNGN;
		} else {
			throw new IllegalArgumentException();
		}
	}

}
