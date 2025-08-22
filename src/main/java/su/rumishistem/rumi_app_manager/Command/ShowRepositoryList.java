package su.rumishistem.rumi_app_manager.Command;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import su.rumishistem.rumi_app_manager.Module.RepositoryList;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class ShowRepositoryList {
	public static void main(String[] args) {
		for (String url:RepositoryList.get_list()) {
			LOG(LOG_TYPE.INFO, url);
		}
	}
}
