package su.rumishistem.rumi_app_manager.Module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryList {
	private static File f = null;
	private static List<String> list = new ArrayList<String>();
	
	public static void init() throws IOException {
		f = new File("/etc/ram/repository");

		if (!f.exists()) f.createNewFile();

		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
	}

	public static boolean exists(String url) {
		for (String row:list) {
			if (row.equalsIgnoreCase(url)) {
				return true;
			}
		}

		return false;
	}

	public static void add(String url) throws IOException {
		list.add(url);
		save();
	}

	public static void save() throws IOException {
		FileWriter fw = new FileWriter(f, true);
		for (String url:list) {
			fw.write(url + "\n");
		}
		fw.close();
	}
}
