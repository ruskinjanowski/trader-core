package com.trader.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class LoggingUtil {
	public static void appendToFile(File file, String text) {
		try {
			Files.write(Paths.get(file.getAbsolutePath()), (text + "\n").getBytes(), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
