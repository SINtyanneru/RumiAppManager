package su.rumishistem.rumi_app_manager.Command;

import static su.rumishistem.rumi_java_lib.LOG_PRINT.Main.LOG;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import su.rumishistem.rumi_app_manager.Module.SQL;
import su.rumishistem.rumi_java_lib.LOG_PRINT.LOG_TYPE;

public class Init {
	private static List<HashMap<String, String>> script_list = new ArrayList<HashMap<String,String>>(){
		{
			add(new HashMap<String, String>(){
				{
					put("NAME", "REPOSITORY");
					put("SCRIPT", """
						CREATE TABLE IF NOT EXISTS `REPOSITORY` (
							`ID` BIGINT NOT NULL PRIMARY KEY,
							`URL` TEXT NOT NULL
						);
					""");
				}
			});
			add(new HashMap<String, String>(){
				{
					put("NAME", "PACKAGE");
					put("SCRIPT", """
						CREATE TABLE IF NOT EXISTS `PACKAGE` (
							`ID` BIGINT NOT NULL PRIMARY KEY,
							`REPOSITORY` BIGINT NOT NULL,
							`NAME` TEXT NOT NULL,
							`DESCRIPTION` TEXT NOT NULL,

							FOREIGN KEY (`REPOSITORY`) REFERENCES REPOSITORY(`ID`) ON DELETE CASCADE
						);
					""");
				}
			});
			add(new HashMap<String, String>(){
				{
					put("NAME", "VERSION");
					put("SCRIPT", """
						CREATE TABLE IF NOT EXISTS `VERSION` (
							`ID` BIGINT NOT NULL PRIMARY KEY,
							`PACKAGE` BIGINT NOT NULL,
							`NAME` TEXT NOT NULL,

							FOREIGN KEY (`PACKAGE`) REFERENCES PACKAGE(`ID`) ON DELETE CASCADE
						);
					""");
				}
			});
			add(new HashMap<String, String>(){
				{
					put("NAME", "DEPENDENCE");
					put("SCRIPT", """
						CREATE TABLE IF NOT EXISTS `DEPENDENCE` (
							`ID` BIGINT NOT NULL PRIMARY KEY,
							`VERSION` BIGINT NOT NULL,
							`DEPENDENCE_PACKAGE` BIGINT NOT NULL,

							FOREIGN KEY (`VERSION`) REFERENCES VERSION(`ID`) ON DELETE CASCADE,
							FOREIGN KEY (`DEPENDENCE_PACKAGE`) REFERENCES PACKAGE(`ID`) ON DELETE CASCADE
						);
					""");
				}
			});
			add(new HashMap<String, String>(){
				{
					put("NAME", "INSTALL");
					put("SCRIPT", """
						CREATE TABLE IF NOT EXISTS `INSTALL` (
							`ID` BIGINT NOT NULL PRIMARY KEY,
							`VERSION` BIGINT NOT NULL,
							`PACKAGE` BIGINT NOT NULL,

							FOREIGN KEY (`VERSION`) REFERENCES VERSION(`ID`) ON DELETE CASCADE,
							FOREIGN KEY (`PACKAGE`) REFERENCES PACKAGE(`ID`) ON DELETE CASCADE
						);
					""");
				}
			});
		}
	};

	public static void main(String[] args) throws SQLException {
		for (HashMap<String, String> script:script_list) {
			LOG(LOG_TYPE.PROCESS, script.get("NAME")+"テーブルを作成中");
			Statement stmt = SQL.get_connection().createStatement();
			stmt.execute(script.get("SCRIPT"));
			LOG(LOG_TYPE.PROCESS_END_OK, "");
		}
	}
}
