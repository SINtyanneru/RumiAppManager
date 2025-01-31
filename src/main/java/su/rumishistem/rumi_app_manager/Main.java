package su.rumishistem.rumi_app_manager;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import su.rumishistem.rumi_app_manager.TYPE.RUNMODE;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Main {
	public static final String OSName = System.getProperty("os.name");

	public static void main(String[] args) throws IOException {
		RUNMODE RUN = null;
		String TARGET = null;

		//初期化
		if (!Files.exists(Path.of("/etc/ram"))) {
			Files.createDirectories(Path.of("/etc/ram"));
		}

		//引数解析
		for (int I = 0; I < args.length; I++) {
			String arg = args[I];
			switch (arg) {
				case "add":{
					RUN = RUNMODE.ADD;
					TARGET = args[I + 1];
					I++;
					break;
				}

				default: {
					LOG(LOG_TYPE.FAILED, arg + "という引数はありません");
					System.exit(1);
				}
			}
		}

		//実行モード
		switch (RUN) {
			case ADD: {
				ADD.Main(TARGET);
			}
		}
	}
}
