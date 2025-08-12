package su.rumishistem.rumi_app_manager;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.nio.file.Files;
import java.nio.file.Path;

import su.rumishistem.rumi_app_manager.Command.Add;
import su.rumishistem.rumi_app_manager.Command.Help;
import su.rumishistem.rumi_app_manager.Module.RepositoryList;
import su.rumishistem.rumi_app_manager.Type.RunMode;
import su.rumishistem.rumi_java_lib.EXCEPTION_READER;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Main {
	public static void main(String[] args) {
		try {
			//new Packer().pack("test", "1.0", new File("/home/rumisan/Documents/test_pkg"));
			//new Unpacker(new File("test.rpk")).show_info();

			if (!Files.exists(Path.of("/etc/ram"))) {
				Files.createDirectory(Path.of("/etc/ram"));
			}

			RepositoryList.init();

			check_root();

			RunMode run = RunMode.None;

			//引数を解析
			for (String arg:args) {
				switch (arg) {
					case "help": {
						run = RunMode.Help;
						break;
					}

					case "add": {
						run = RunMode.Add;
						break;
					}
				}
			}

			switch (run) {
				case Help: {
					Help.main(args);
					return;
				}

				case Add: {
					Add.main(args);
					return;
				}

				default: {
					System.out.println("helpでヘルプを表示可能です。");
					return;
				}
			}
		} catch (RuntimeException ex) {
			System.err.println(ex.getMessage());
		} catch (Exception EX) {
			String ex_text = EXCEPTION_READER.READ(EX);

			for (String line:ex_text.split("\n")) {
				LOG(LOG_TYPE.FAILED, line);
			}
		}
	}

	private static void check_root() {
		String user = System.getProperty("user.name");
		if (!"root".equals(user)) {
			throw new RuntimeException("管理者権限で実行してください。");
		}
	}
}
