package su.rumishistem.rumi_app_manager.Command;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.text.Charsets;
import su.rumishistem.rumi_app_manager.Module.RepositoryList;
import su.rumishistem.rumi_app_manager.Module.SQL;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.SnowFlake;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Update {
	public static void main(String[] args) throws MalformedURLException, SQLException {
		//ダウンロード
		for (String url:RepositoryList.get_list()) {
			LOG(LOG_TYPE.PROCESS, "取得:" + new URL(url).getHost());

			try {
				FETCH ajax = new FETCH(url.toString() + "PACKAGE");
				FETCH_RESULT result = ajax.GET();
				LOG(LOG_TYPE.PROCESS_END_OK, "");

				List<HashMap<String, String>> package_list = parse_package_list(result.getString(Charsets.UTF_8));

				//リポジトリのID取得
				List<HashMap<String, Object>> id_result = SQL.run("SELECT `ID` FROM `REPOSITORY` WHERE `URL` = ?;", new Object[] {url.toString()});
				if (id_result.size() == 0) throw new RuntimeException("は？");
				String repository_id = String.valueOf((long)id_result.get(0).get("ID"));

				for (HashMap<String, String> pkg:package_list) {
					List<HashMap<String, Object>> pkg_result = SQL.run("SELECT `ID` FROM `PACKAGE` WHERE `REPOSITORY` = ? AND `NAME` = ?;", new Object[] {
						repository_id, pkg.get("NAME")
					});

					String pkg_id = null;
					if (pkg_result.size() == 0) {
						//データベースに無いパッケージ
						pkg_id = String.valueOf(SnowFlake.GEN());
						SQL.up_run("""
							INSERT
								INTO `PACKAGE` (`ID`, `REPOSITORY`, `NAME`, `DESCRIPTION`)
							VALUES
								(?, ?, ?, ?);
						""", new Object[] {
							pkg_id, repository_id, pkg.get("NAME"), pkg.get("DESCRIPTION")
						});
					} else {
						pkg_id = String.valueOf((long)pkg_result.get(0).get("ID"));
					}

					if (SQL.run("SELECT `ID` FROM `VERSION`WHERE `PACKAGE` = ? AND `NAME` = ?;", new Object[] {pkg_id, (String)pkg.get("VERSION")}).size() == 0) {
						SQL.up_run("""
							INSERT
								INTO `VERSION` (`ID`, `PACKAGE`, `NAME`)
							VALUES
								(?, ?, ?)
						""", new Object[] {
							String.valueOf(SnowFlake.GEN()), pkg_id, (String)pkg.get("VERSION")
						});
					}
				}
			} catch (IOException EX) {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
			}
		}
	}

	private static List<HashMap<String, String>> parse_package_list(String body) {
		List<HashMap<String, String>> package_list = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> pkg = null;
		String pkg_name = null;
		for (String line:body.split("\n")) {
			if (line.startsWith("[") && line.endsWith("]")) {
				if (pkg_name != null) {
					pkg.put("NAME", pkg_name.toUpperCase());
					package_list.add(pkg);
				}
				pkg = new HashMap<String, String>();

				pkg_name = line.replace("[", "").replace("]", "");
			} else {
				int index = line.indexOf("=");
				if (index == -1) throw new RuntimeException("Format error");
				String key = line.substring(0, index);
				String value = line.substring(index+1);
				pkg.put(key, value);
			}
		}
		pkg.put("NAME", pkg_name);
		package_list.add(pkg);

		return package_list;
	}
}
