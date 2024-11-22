package com.rumisystem.rumi_app_manager;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class INSTALL {
	public static void Main(String PKG_NAME) throws IOException {
		JsonNode PKG_LIST = new ObjectMapper().readTree(new File("/etc/ram/package.json"));

		LOG(LOG_TYPE.INFO, "パッケージを検索しています");

		List<JsonNode> HIT_PKG_LIST = new ArrayList<JsonNode>();
		List<String> HIT_TEXT = new ArrayList<String>();
		for (int I = 0; I < PKG_LIST.size(); I++) {
			JsonNode PKG = PKG_LIST.get(I);
			if (PKG.get("PACKAGE").get("NAME").asText().equals(PKG_NAME)) {
				LOG(LOG_TYPE.INFO, "ヒット：" + PKG.get("REPOSITORY").asText());
				HIT_PKG_LIST.add(PKG);
				HIT_TEXT.add(PKG.get("REPOSITORY").asText());
			}
		}
		LOG(LOG_TYPE.OK, "完了");
		LOG(LOG_TYPE.OK, "");
		LOG(LOG_TYPE.OK, "");

		//見つかったか
		if (HIT_PKG_LIST.size() != 0) {
			LOG(LOG_TYPE.INFO, "次のパッケージが見つかりました");

			if (HIT_PKG_LIST.size() == 1) {
				//1つしか見つからなかった場合
				LOG(LOG_TYPE.INFO, HIT_TEXT.get(0));
				LOG(LOG_TYPE.INFO, "インストールしますか？");
				System.out.print("[y/n]>");

				if (new Scanner(System.in).nextLine().equals("y")) {
					INST(HIT_PKG_LIST.get(0));
				} else {
					System.out.println("キャンセルしました");
				}
			} else {
				//複数ある場合
				for (String HIT:HIT_TEXT) {
					LOG(LOG_TYPE.INFO, HIT);
				}
				LOG(LOG_TYPE.INFO, "どれをインストールしますか？番号で指定してください");
				System.out.print(">");

				int I = Integer.parseInt(new Scanner(System.in).nextLine());
				INST(HIT_PKG_LIST.get(I));
			}
		} else {
			//みつからんかった
			LOG(LOG_TYPE.INFO, "パッケージは見つかりませんでした");
		}
	}

	//実際にインスコする部分
	private static void INST(JsonNode PKG) {
		
	}
}
