package su.rumishistem.rumi_app_manager.MODULE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppManager {
	private static final String AppListPath = "/etc/ram/AppList.json";

	public static void Init() throws IOException, URISyntaxException {
		//ファイルが無ければ作る
		if (!Files.exists(Path.of(AppListPath))) {
			File F = new File(AppListPath);
			FileOutputStream FOS = new FileOutputStream(F);
			FOS.write("[]".getBytes());
			FOS.flush();
			FOS.close();
		}
	}

	public static void ClearList() throws IOException {
		Files.delete(Path.of(AppListPath));
		FileOutputStream FOS = new FileOutputStream(new File(AppListPath));
		FOS.write("[]".getBytes());
		FOS.flush();
		FOS.close();
	}

	public static void Save(String Data) throws IOException {
		FileOutputStream FOS = new FileOutputStream(new File(AppListPath));
		FOS.write(Data.getBytes());
		FOS.flush();
		FOS.close();
	}
}
