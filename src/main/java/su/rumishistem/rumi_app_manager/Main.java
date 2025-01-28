package su.rumishistem.rumi_app_manager;

public class Main {
	public static void main(String[] args) {
		try {
			REPOSITORY.INIT();

			switch (args[0]) {
				case "factory": {
					FACTORY.Main();
					return;
				}

				case "add": {
					if (args[1] != null) {
						ADD.Main(args[1]);
					} else {
						System.out.println("引数が足りません");
						System.exit(1);
					}
					return;
				}

				case "update": {
					UPDATE.Main();
					return;
				}

				case "install": {
					INSTALL.Main(args[1]);
					return;
				}

				default: {
					System.out.println("?");
				}
			}
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}
}
