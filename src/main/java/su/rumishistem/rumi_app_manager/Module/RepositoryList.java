package su.rumishistem.rumi_app_manager.Module;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import su.rumishistem.rumi_java_lib.SnowFlake;

public class RepositoryList {
	private static List<String> list = new ArrayList<String>();
	
	public static void init() throws SQLException {
		List<HashMap<String, Object>> result = SQL.run("SELECT * FROM `REPOSITORY`", null);

		for (HashMap<String, Object> row:result) {
			list.add((String)row.get("URL"));
		}
	}

	public static boolean exists(String url) {
		for (String row:list) {
			if (row.equalsIgnoreCase(url)) {
				return true;
			}
		}

		return false;
	}

	public static void add(String url) throws SQLException {
		list.add(url);
		save();
	}

	public static String[] get_list() {
		String[] sl = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			sl[i] = list.get(i);
		}

		return sl;
	}

	public static void save() throws SQLException {
		for (String url:list) {
			SQL.up_run("INSERT INTO `REPOSITORY` (`ID`, `URL`) VALUES (?, ?)", new Object[] {SnowFlake.GEN(), url});
		}
	}
}
