package com.rumisystem.rumi_app_manager;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumi_java_lib.FETCH;
import com.rumisystem.rumi_java_lib.FETCH_RESULT;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class UPDATE {
	public static void Main() throws IOException {
		List<Object> PACKAGE_LIST = new ArrayList<Object>();

		//全リポジトリから取得していく
		for (String REPO_URL:REPOSITORY.GetLIST()) {
			LOG(LOG_TYPE.PROCESS, "取得：" + REPO_URL);

			FETCH AJAX = new FETCH(REPO_URL + "list.json");
			FETCH_RESULT RESULT = AJAX.GET();
			if (RESULT.GetSTATUS_CODE() == 200) {
				LOG(LOG_TYPE.PROCESS_END_OK, "");
				LOG(LOG_TYPE.PROCESS, "解析中");
				//解析
				JsonNode DATA = new ObjectMapper().readTree(RESULT.GetString());
				for (int I = 0; I < DATA.size(); I++) {
					JsonNode ROW = DATA.get(I);
					LinkedHashMap<String, Object> PACKAGE = new LinkedHashMap<String, Object>();
					PACKAGE.put("REPOSITORY", REPO_URL);
					PACKAGE.put("PACKAGE", ROW);
					PACKAGE_LIST.add(PACKAGE);
				}
				LOG(LOG_TYPE.PROCESS_END_OK, "解析中");
			} else {
				LOG(LOG_TYPE.PROCESS_END_FAILED, "");
				LOG(LOG_TYPE.FAILED, "取得できないリポジトリがありました：" + RESULT.GetSTATUS_CODE());
			}
		}

		//ファイルとして書き出し
		FileWriter FW = new FileWriter("/etc/ram/package.json");
		FW.write(new ObjectMapper().writeValueAsString(PACKAGE_LIST));
		FW.close();

		LOG(LOG_TYPE.OK, "");
		LOG(LOG_TYPE.OK, "パケージリストを更新しました！計" + PACKAGE_LIST.size() + "個のパケージが見つかりました");
		LOG(LOG_TYPE.OK, "");
	}
}
