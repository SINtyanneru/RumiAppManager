package su.rumishistem.rumi_app_manager;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.IOException;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumi_java_lib.FETCH;
import com.rumisystem.rumi_java_lib.FETCH_RESULT;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class ADD {
	public static void Main(String REPO_URL) throws IOException {
		//既に追加済みならスキップ
		if (REPOSITORY.isExists(REPO_URL)) {
			LOG(LOG_TYPE.INFO, "既に存在します");
			System.exit(1);
		}

		FETCH AJAX = new FETCH(REPO_URL + "info.json");
		FETCH_RESULT RESULT = AJAX.GET();
		if (RESULT.GetSTATUS_CODE() == 200) {
			JsonNode REPO_INFO = new ObjectMapper().readTree(RESULT.GetString());
			LOG(LOG_TYPE.INFO, "次のリポジトリを追加します");
			LOG(LOG_TYPE.INFO, "-----------------------------------------");
			LOG(LOG_TYPE.INFO, "名前：" + REPO_INFO.get("NAME").asText());
			LOG(LOG_TYPE.INFO, "説明：" + REPO_INFO.get("DESC").asText());
			LOG(LOG_TYPE.INFO, "-----------------------------------------");
			System.out.print("続行しますか？[y/n]");
			if (new Scanner(System.in).nextLine().equals("y")) {
				REPOSITORY.AddREPO(REPO_URL);
				LOG(LOG_TYPE.OK, "追加しました、お好みでパケージリストを更新してください");
			} else {
				LOG(LOG_TYPE.INFO, "キャンセルしました");
			}
		} else {
			LOG(LOG_TYPE.FAILED, "リポジトリ情報を取得できませんでした：" + RESULT.GetSTATUS_CODE());
		}
	}
}
