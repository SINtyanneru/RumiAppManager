package com.rumisystem.rumi_app_manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class REPOSITORY {
	private static String FILE_PATH = "/etc/ram/repository";
	private static List<String> LIST = new ArrayList<String>();

	public static void INIT() throws IOException {
		BufferedReader BR = new BufferedReader(new FileReader(FILE_PATH));
		String LINE;
		while ((LINE = BR.readLine()) != null) {
			LIST.add(LINE);
		}
		BR.close();
	}

	/**
	 * リポジトリリストに、リポジトリが存在するかチェックする
	 * @param REPO_URL リポジトリのURL
	 * @return
	 */
	public static boolean isExists(String REPO_URL) {
		for (String URL:LIST) {
			if (URL.equals(REPO_URL)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * リポジトリを追加する
	 * @param REPO_URL リポジトリのURL
	 * @throws IOException 
	 */
	public static void AddREPO(String REPO_URL) throws IOException {
		//リストに追加
		LIST.add(REPO_URL);

		//ファイルに書き出し
		FileWriter FW = new FileWriter(FILE_PATH);
		FW.write(String.join("\n", LIST));
		FW.close();
	}

	public static List<String> GetLIST() {
		return LIST;
	}
}
