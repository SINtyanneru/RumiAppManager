package su.rumishistem.rumi_app_manager.Command;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import su.rumishistem.rumi_app_manager.Module.RepositoryList;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Update {
	public static void main(String[] args) throws MalformedURLException {
		StringBuilder download_packagelist = new StringBuilder();

		try {
			//ダウンロード
			for (String url:RepositoryList.get_list()) {
				LOG(LOG_TYPE.PROCESS, "取得:" + new URL(url).getHost());

				try {
					FETCH ajax = new FETCH(url);
					FETCH_RESULT result = ajax.GET();

					LOG(LOG_TYPE.PROCESS_END_OK, "");

					download_packagelist.append(new URL(url).getHost() + "=" + Base64.getEncoder().encodeToString(result.getRaw()));
				} catch (IOException EX) {
					LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				}
			}

			//解析
			System.out.println(download_packagelist);
		} finally {
			//ガベコレ
			download_packagelist = null;
		}
	}
}
