package su.rumishistem.rumi_app_manager;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import su.rumishistem.rumi_app_manager.MODULE.RepoManager;
import su.rumishistem.rumi_app_manager.TYPE.RUNMODE;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Main {
	public static final String OSName = System.getProperty("os.name");

	public static void main(String[] args) {
		try {
			RUNMODE RUN = null;
			String TARGET = null;

			//初期化
			DirInit();
			RepoManager.Init();

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
						LOG(LOG_TYPE.FAILED, arg + "←は？");
						System.exit(1);
					}
				}
			}

			//実行する
			if (RUN != null) {
				switch (RUN) {
					case ADD: {
						ADD.Main(TARGET);
					}
				}
			} else {
				System.out.println("ヘルプは作ってない");
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	private static void DirInit() throws IOException {
		String[] DirList = new String[] {
			"/etc/ram",
			"/var/ram"
		};

		//ディレクトリ
		for (String P:DirList) {
			if (!Files.exists(Path.of(P))) {
				Files.createDirectories(Path.of(P));
			}
		}
	}
}
