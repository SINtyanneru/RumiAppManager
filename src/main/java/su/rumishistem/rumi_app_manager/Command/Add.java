package su.rumishistem.rumi_app_manager.Command;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import kotlin.text.Charsets;
import su.rumishistem.rumi_app_manager.Module.INIParser;
import su.rumishistem.rumi_app_manager.Module.RepositoryList;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.Option.OptionRunnable;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Add {
	public static void main(String[] args) throws IOException {
		if (args.length < 2) throw new RuntimeException("引数が足りません。");

		URL url;

		//URLチェック
		try {
			url = new URL(args[1]);
		} catch (MalformedURLException EX) {
			throw new RuntimeException("リポジトリのURLが不正です");
		}

		//既にある？
		if (RepositoryList.exists(url.toString())) {
			LOG(LOG_TYPE.INFO, "既に追加済みです");
			return;
		}

		//取得
		LOG(LOG_TYPE.PROCESS, "取得:" + url.getHost());
		FETCH ajax = new FETCH("https://" + url.getHost() + ":" + url.getPort() + url.getPath() + "INFO");
		FETCH_RESULT result = ajax.GET();
		String body = result.getString(Charsets.UTF_8);
		LOG(LOG_TYPE.PROCESS_END_OK, "");

		//INIを解析
		INIParser ini = new INIParser(body);
		ini.get("REPOSITORY", "NAME").match(new OptionRunnable<String>() {
			@Override
			public void Some(String v) {
				try {
					LOG(LOG_TYPE.PROCESS, v + "を追加しています...");
					RepositoryList.add(url.toString());
					LOG(LOG_TYPE.PROCESS_END_OK, "");
				} catch (SQLException EX) {
					EX.printStackTrace();
				}
			}
			
			@Override
			public void None(Exception ex) {
				LOG(LOG_TYPE.FAILED, "INFOに構文ミスがあります。");
				LOG(LOG_TYPE.FAILED, ex.getMessage());
				LOG(LOG_TYPE.FAILED, "リポジトリ管理者に問い合わせてください。");
				System.exit(1);
			}
		});
	}
}
