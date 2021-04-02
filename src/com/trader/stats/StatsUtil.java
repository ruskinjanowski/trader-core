package com.trader.stats;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trader.definitions.TraderFolders;
import com.trader.utility.MarketData;

public class StatsUtil
{
	static final File saveFile = new File(TraderFolders.getBase(), "statsHistory.txt");
	static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	static final StatsHistory statsHistory = loadHistory();

	static StatsCollector statsCollector = new StatsCollector();

	public static void start() {
		statsCollector.start();
	}

	public static void stop() {
		statsCollector.interrupt();
		saveHistory(statsHistory);
	}

	static void saveHistory(StatsHistory h) {
		String json = gson.toJson(h);
		try {
			File parent = saveFile.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Couldn't create dir: " + parent);
			}
			Files.write(saveFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	static StatsHistory loadHistory() {
		try {
			String json = new String(Files.readAllBytes(saveFile.toPath()), StandardCharsets.UTF_8);
			StatsHistory fromJson = gson.fromJson(json, StatsHistory.class);
			return fromJson;
		}
		catch (NoSuchFileException e) {

			return new StatsHistory();
		}
		catch (IOException e) {
			e.printStackTrace();
			return new StatsHistory();
		}
	}

	public static SdResult getSd() {
		return statsHistory.calculateSd();
	}

	public static boolean hasData() {
		return statsHistory.prices.size() > 10;
	}

	private static class StatsCollector extends Thread
	{
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(2 * 60 * 1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				double diffEur_perc = MarketData.INSTANCE.getDiffEur_perc(1.0);
				statsHistory.addPoint(diffEur_perc);

				saveHistory(statsHistory);
			}
		}
	}

}
