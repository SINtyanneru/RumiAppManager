package su.rumishistem.rumi_app_manager.Module;

import java.util.HashMap;

import su.rumishistem.rumi_java_lib.Option;

public class INIParser {
	private HashMap<String, HashMap<String, String>> tree = new HashMap<String, HashMap<String,String>>();

	public INIParser(String body) {
		String[] line_list = body.split("\n");

		String root_name = null;

		for (String line:line_list) {
			if (line.startsWith("[") && line.endsWith("]")) {
				//[FUCK]←これ
				root_name = line.replace("[", "").replace("]", "");
				tree.put(root_name, new HashMap<String, String>());
			} else {
				//FUCK=SHIT←これ
				int equal_position = line.indexOf("=");
				String key = line.substring(0, equal_position);
				String val = line.substring(equal_position + 1);

				if (tree.get(root_name) == null) tree.put(root_name, new HashMap<String, String>());

				tree.get(root_name).put(key, val);
			}
		}
	}

	public Option<String> get(String root, String key) {
		if (tree.get(root) == null) {
			return new Option<String>(new RuntimeException("rootがない"));
		}

		if (tree.get(root).get(key) == null) {
			return new Option<String>(new RuntimeException("keyがない"));
		}

		return new Option<String>(tree.get(root).get(key));
	}
}
