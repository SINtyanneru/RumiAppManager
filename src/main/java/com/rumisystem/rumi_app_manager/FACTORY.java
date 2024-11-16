package com.rumisystem.rumi_app_manager;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class FACTORY {
	public static void Main() {
		Scanner IN = new Scanner(System.in);
		System.out.println("------------------------------[ パケージ工場 ]------------------------------");

		System.out.print("あなたの名前:");
		String NAME = IN.nextLine();

		System.out.print("あなたのホームページ:");
		String HP_URL = IN.nextLine();

		System.out.print("今回作るパッケージ名:");
		String PACKAGE_NAME = IN.nextLine();

		System.out.print("パッケージのバージョン:");
		String PAKCAGE_VERSION = IN.nextLine();

		System.out.print("リリース先のOS(Linux/Windows/Android)：");
		String OS_NAME = IN.nextLine();

		System.out.print("インストールスクリプトのパス(ない場合は無)：");
		String INSTALL_PATH = IN.nextLine();

		System.out.print("アンインストールスクリプトのパス(ない場合は無)：");
		String UNINSTALL_PATH = IN.nextLine();

		System.out.print("パッケージのデータのパス：");
		String DATA_PATH = IN.nextLine();

		System.out.print("入力した情報は正しいですか？ [y/n]");
		if (IN.next().equals("y")) {
			LOG(LOG_TYPE.INFO, "パッケージングを開始します");
			try {
				String TEMP_PATH = "/tmp/" + UUID.randomUUID().toString() + "/";

				//インストールスクリプトをコピー
				if (!INSTALL_PATH.equals("")) {
					Files.copy(Paths.get(INSTALL_PATH), Paths.get(TEMP_PATH + "install.py"), StandardCopyOption.REPLACE_EXISTING);
					LOG(LOG_TYPE.OK, "インストールスクリプトをコピー");
				}

				//アンインストールスクリプトをコピー
				if (!UNINSTALL_PATH.equals("")) {
					Files.copy(Paths.get(UNINSTALL_PATH), Paths.get(TEMP_PATH + "uninstall.py"), StandardCopyOption.REPLACE_EXISTING);
					LOG(LOG_TYPE.OK, "アンインストールスクリプトをコピー");
				}

				//本命のファイルをコピーする
				LOG(LOG_TYPE.INFO, "ファイルのコピーを開始。。。");
				Path DATA_PATHS = Paths.get(DATA_PATH);
				Files.walkFileTree(Paths.get(DATA_PATH), new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path DIR, BasicFileAttributes ATTRS) throws IOException {
						//サブディレクトリ作成
						Path TARGET_PATH = Paths.get(TEMP_PATH + "data").resolve(DATA_PATHS.relativize(DIR));
						Files.createDirectories(TARGET_PATH);
						LOG(LOG_TYPE.OK, "作成：" + TARGET_PATH.toString());

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path FILE, BasicFileAttributes ATTRS) throws IOException {
						//ファイルをコピー
						Path TARGET_PATH = Paths.get(TEMP_PATH + "data").resolve(DATA_PATHS.relativize(FILE));
						Files.copy(FILE, TARGET_PATH);
						LOG(LOG_TYPE.OK, "コピー：" + FILE.toString() + "→" + TARGET_PATH.toString());

						return FileVisitResult.CONTINUE;
					}
				});

				//パッケージ情報をJSONとして書き込む
				LinkedHashMap<String, Object> PACKAGE_INFO_DATA = new LinkedHashMap<String, Object>();
				LinkedHashMap<String, String> PACKAGE_INFO = new LinkedHashMap<String, String>();
				PACKAGE_INFO.put("NAME", PACKAGE_NAME);
				PACKAGE_INFO.put("VERSION", PAKCAGE_VERSION);
				PACKAGE_INFO.put("OS", OS_NAME);
				PACKAGE_INFO_DATA.put("PACKAGE", PACKAGE_INFO);
				LinkedHashMap<String, String> PACKAGE_AUTHOR = new LinkedHashMap<String, String>();
				PACKAGE_AUTHOR.put("NAME", NAME);
				PACKAGE_AUTHOR.put("HP_URL", HP_URL);
				PACKAGE_INFO_DATA.put("AUTHOR", PACKAGE_AUTHOR);
				FileWriter FW = new FileWriter(TEMP_PATH + "info.json");
				FW.write(new ObjectMapper().writeValueAsString(PACKAGE_INFO_DATA));
				FW.close();
				LOG(LOG_TYPE.OK, "パッケージ情報を書き込みました");

				//ZIP化開始
				LOG(LOG_TYPE.PROCESS, "ZIP化しています");
				ZipOutputStream ZIO = new ZipOutputStream(Files.newOutputStream(Paths.get("output.zip")));
				Files.walk(Paths.get(TEMP_PATH)).forEach(new Consumer<Path>() {
					@Override
					public void accept(Path PATH) {
						try {
							Path SOUTAI_PATH = Paths.get(TEMP_PATH).relativize(PATH);
							ZipEntry ENTRY = new ZipEntry(SOUTAI_PATH.toString());
							ZIO.putNextEntry(ENTRY);

							//唯のファイルならコピーする
							if (Files.isRegularFile(PATH)) {
								Files.copy(PATH, ZIO);
							}
							ZIO.closeEntry();
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				});
				ZIO.close();
				LOG(LOG_TYPE.PROCESS_END_OK, "");
			} catch (Exception EX) {
				EX.printStackTrace();
				System.out.println("エラー、パッケージングできませんでした");
			}
		} else {
			System.out.println("やり直せ");
			return;
		}
	}
}
