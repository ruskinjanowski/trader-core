package com.trader.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class LoggingUtil {
	private static final Map<Long, Long> logTimes = new HashMap<>();

	public static void appendToFile(File file, String text) {
		try {
			Files.write(Paths.get(file.getAbsolutePath()), (text + "\n").getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void appendToFile(File file, String text, long logGuid) {
		if (!logTimes.containsKey(logGuid)
				|| logTimes.get(logGuid) < System.currentTimeMillis() - 60 * 1000) {
			logTimes.put(logGuid, System.currentTimeMillis());
			appendToFile(file, text);
		}
	}
}
