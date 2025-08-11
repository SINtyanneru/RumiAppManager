package su.rumishistem.rumi_app_manager.RamPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackingTree {
	private List<File> directory_list = new ArrayList<File>();
	private List<File> file_list = new ArrayList<File>();

	public void add_directory(File f) {
		directory_list.add(f);
	}
	
	public void add_file(File f) {
		file_list.add(f);
	}

	public int get_directory_count() {
		return directory_list.size();
	}

	public int get_file_count() {
		return file_list.size();
	}

	public List<File> get_directory_list() {
		return directory_list;
	}

	public List<File> get_file_list() {
		return file_list;
	}
}
