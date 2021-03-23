package org.openexchangerates.oerjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.Gson;

public class CurrConv
{

	static final Gson gson = new Gson();

	public static Double getEurZar() {
		try {
			String eurZar = eurZar();
			CurrConvResponse fromJson = gson.fromJson(eurZar, CurrConvResponse.class);
			return fromJson.EUR_ZAR;
		}
		catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}
	public static String eurZar() throws IOException {
		URL url = new URL(
				"https://free.currconv.com/api/v7/convert?q=EUR_ZAR&compact=ultra&apiKey=79fc90e627dab2d4c687");

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		StringBuilder sb = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}

		in.close();

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(getEurZar());
	}

	static class CurrConvResponse
	{
		final double EUR_ZAR;

		public CurrConvResponse(double eUR_ZAR) {
			super();
			EUR_ZAR = eUR_ZAR;
		}

	}
}
