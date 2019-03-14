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

public class LimitsAndRates {
	public static final DateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.ENGLISH);

	public final Date date;
	public final double limitUpper;
	public final double limitLower;

	public final double rateUpper;
	public final double rateLower;

	/**
	 * 
	 * @param limitUpper
	 * @param limitLower
	 * @param rateUpper
	 *            ZARtoX
	 * @param rateLower
	 *            XtoZAR
	 */
	public LimitsAndRates(double limitUpper, double limitLower, double rateUpper, double rateLower) {
		this(new Date(), limitUpper, limitLower, rateUpper, rateLower);
	}

	public LimitsAndRates(Date date, double limitUpper, double limitLower, double rateUpper, double rateLower) {
		super();
		this.date = date;
		this.limitUpper = limitUpper;
		this.limitLower = limitLower;
		this.rateUpper = rateUpper;
		this.rateLower = rateLower;
		check();
	}

	private void check() {
		if (limitUpper < limitLower || rateUpper < rateLower) {
			System.out.println(this);
			throw new IllegalStateException();
		}
	}

	@Override
	public String toString() {
		String s = FORMAT.format(date) + "," + limitUpper + "," + limitLower + "," + rateUpper + "," + rateLower;
		return s;
	}

	public static LimitsAndRates fromString(String line) {
		String[] vals = line.split(",");
		Date date = null;
		try {
			date = FORMAT.parse(vals[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		double lupper = Double.parseDouble(vals[1]);
		double llower = Double.parseDouble(vals[2]);
		double rupper = Double.parseDouble(vals[3]);
		double rlower = Double.parseDouble(vals[4]);
		return new LimitsAndRates(date, lupper, llower, rupper, rlower);
	}

	public static List<LimitsAndRates> fromFile(File f) {
		try (Scanner sc = new Scanner(f)) {
			List<LimitsAndRates> lines = new ArrayList<>();
			while (sc.hasNextLine()) {
				lines.add(fromString(sc.nextLine()));
			}
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
