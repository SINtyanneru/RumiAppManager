package su.rumishistem.rumi_app_manager;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumi_app_manager.Main.OSName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_app_manager.MODULE.AppManager;
import su.rumishistem.rumi_app_manager.MODULE.RepoManager;
import su.rumishistem.rumi_app_manager.TYPE.RepositoryInfo;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class UPDATE {
	public static void Main() throws IOException {
		List<LinkedHashMap<String, Object>> AppListBody = new ArrayList<LinkedHashMap<String,Object>>();

		for (RepositoryInfo R:RepoManager.GetList()) {
			String ListURL = R.GetURI().toString() + "list.json";
			LOG(LOG_TYPE.PROCESS, "アプリ一覧取得：" + ListURL);
			try {
				FETCH Ajax = new FETCH(ListURL);
				FETCH_RESULT Result = Ajax.GET();
				if (Result.GetSTATUS_CODE() == 200) {
					LOG(LOG_TYPE.PROCESS_END_OK, "");
					JsonNode Body = new ObjectMapper().readTree(Result.GetString());
					LOG(LOG_TYPE.INFO, "アプリ数：" + Body.size());

					for (int I = 0; I < Body.size(); I++) {
						JsonNode App = Body.get(I);
						if (App.get("PACKAGE").get(OSName) == null) continue;

						LinkedHashMap<String, Object> AppBody = new LinkedHashMap<String, Object>();
						AppBody.put("REPOSITORY", R.GetURI().getHost().toString());
						AppBody.put("APP", App);
						AppListBody.add(AppBody);
					}
				} else {
					LOG(LOG_TYPE.PROCESS_END_FAILED, "");
					LOG(LOG_TYPE.FAILED, "リポジトリが" + Result.GetSTATUS_CODE() + "を返しました");
				}
			} catch (Exception EX) {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				EX.printStackTrace();
				System.exit(1);
			}
		}

		AppManager.ClearList();
		AppManager.Save(new ObjectMapper().writeValueAsString(AppListBody));
	}
}
