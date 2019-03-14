package arbtrader.credentials;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TraderFolders {
	public enum ProgramName {
		EventSever, SpreadTrader, LunoLuno, BasicArbTrader, LunoBitstamp
	}

	private static final File BASE = getBaseFolder();
	private static final File CONFIG = new File(BASE, "config");
	private static final File LOGGING = new File(BASE, "logging");

	public static File getConfig(ProgramName program) {
		File f = new File(CONFIG, program.toString());
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

	public static File getLogging(ProgramName program) {
		File f = new File(LOGGING, program.toString());
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

	private static File getBaseFolder() {
		File f = new File("");
		Path p = Paths.get(f.getAbsolutePath());
		File d = new File(p.getRoot().toString(), "trader");

		return d;
	}

	public static void main(String[] args) {
		System.out.println(CONFIG);
		System.out.println(LOGGING);
	}
}
