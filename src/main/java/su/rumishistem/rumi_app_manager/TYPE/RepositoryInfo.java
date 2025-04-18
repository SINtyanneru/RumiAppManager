package su.rumishistem.rumi_app_manager.TYPE;

import java.net.URI;
import java.util.LinkedHashMap;

public class RepositoryInfo {
	private URI URL;

	public RepositoryInfo(URI URL) {
		this.URL = URL;
	}

	public URI GetURI() {
		return URL;
	}

	public LinkedHashMap<String, Object> GenHM() {
		LinkedHashMap<String, Object> HM = new LinkedHashMap<String, Object>();
		HM.put("URL", URL);
		return HM;
	}
}
