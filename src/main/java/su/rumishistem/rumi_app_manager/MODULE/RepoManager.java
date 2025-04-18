package su.rumishistem.rumi_app_manager.MODULE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.rumi_app_manager.TYPE.RepositoryInfo;

public class RepoManager {
	private static final String RepoListPath = "/etc/ram/RepoList.json";
	private static List<RepositoryInfo> RepoList = new ArrayList<RepositoryInfo>();

	public static void Init() throws IOException, URISyntaxException {
		//ファイルが無ければ作る
		if (!Files.exists(Path.of(RepoListPath))) {
			File F = new File(RepoListPath);
			F.createNewFile();
			FileOutputStream FOS = new FileOutputStream(F);
			FOS.write("[]".getBytes());
			FOS.flush();
			FOS.close();
		} else {
			//ファイルをロード
			JsonNode RLFileData = new ObjectMapper().readTree(new File(RepoListPath));
			for (int I = 0; I < RLFileData.size(); I++) {
				JsonNode ROW = RLFileData.get(I);
				RepoList.add(new RepositoryInfo(new URI(ROW.get("URL").asText())));
			}
		}
	}

	public static boolean RepoExists(String Host) {
		for (RepositoryInfo R:RepoList) {
			if (R.GetURI().getHost().equals(Host)) {
				return true;
			}
		}

		return false;
	}

	public static List<RepositoryInfo> GetList() {
		return RepoList;
	}

	public static void AddRepo(String URL) throws URISyntaxException, JsonProcessingException, IOException {
		RepoList.add(new RepositoryInfo(new URI(URL)));
		SaveRepoFile();
	}

	private static void SaveRepoFile() throws JsonProcessingException, IOException{
		List<LinkedHashMap<String, Object>> RepoHMList = new ArrayList<LinkedHashMap<String,Object>>();

		for (RepositoryInfo R:RepoList) {
			RepoHMList.add(R.GenHM());
		}

		FileOutputStream FOS = new FileOutputStream(new File(RepoListPath));
		FOS.write(new ObjectMapper().writeValueAsBytes(RepoHMList));
		FOS.flush();
		FOS.close();
	}
}
