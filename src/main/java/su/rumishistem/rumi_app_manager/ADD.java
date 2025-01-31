package su.rumishistem.rumi_app_manager;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_app_manager.MODULE.InputRead;
import su.rumishistem.rumi_java_lib.FETCH;
import su.rumishistem.rumi_java_lib.FETCH_RESULT;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class ADD {
	public static void Main(String URL) throws IOException {
		LOG(LOG_TYPE.PROCESS, URL + "に問い合わせています。。。");

		FETCH AJAX = new FETCH(URL + "/info.json");
		FETCH_RESULT RESULT = AJAX.GET();
		if (RESULT.GetSTATUS_CODE() == 200) {
			JsonNode INFO = new ObjectMapper().readTree(RESULT.GetRAW());

			LOG(LOG_TYPE.PROCESS_END_OK, "");
			LOG(LOG_TYPE.OK, "名前:" + INFO.get("NAME").asText());
			LOG(LOG_TYPE.OK, "説明:" + INFO.get("DESC").asText());
			System.out.print("このリポジトリを追加しますか？ [y/n]>");

			if (InputRead.Read().equals("y")) {
				LOG(LOG_TYPE.PROCESS, "追加しています...");
			} else {
				System.exit(0);
			}
		} else {
			LOG(LOG_TYPE.PROCESS_END_FAILED, "");
			LOG(LOG_TYPE.FAILED, "指定されたリポジトリは、Ram用ではありません！");
		}
	}
}
