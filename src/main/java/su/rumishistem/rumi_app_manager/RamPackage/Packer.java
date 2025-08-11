package su.rumishistem.rumi_app_manager.RamPackage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Packer {
	private String base_path = null;

	public void pack(String package_name, String package_version, File package_dir) throws IOException {
		if (!package_dir.exists()) throw new RuntimeException("指定されたパスはこの世に存在しません。");
		if (!package_dir.isDirectory()) throw new RuntimeException("指定されたパスはディレクトリではありません。");

		base_path = package_dir.getPath();

		//ファイルとディレクトリを収録
		PackingTree tree = new PackingTree();
		search_directory(tree, package_dir);

		//バイナリ化
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(header_field(package_name, package_version, tree.get_directory_count(), tree.get_file_count()));

		//ディレクトリ集計
		for (File d:tree.get_directory_list()) {
			baos.write(directory_field(d));
		}

		//ファイル集計
		for (File f:tree.get_file_list()) {
			baos.write(file_field(f));
		}

		Files.write(Path.of("test.rpk"), baos.toByteArray());
	}

	private void search_directory(PackingTree tree,File dir) {
		File[] file_list = dir.listFiles();
		for (File f:file_list) {
			if (f.isDirectory()) {
				System.out.println("[DIR ]" + f.getPath());
				tree.add_directory(f);
			} else {
				System.out.println("[FILE]" + f.getPath());
				tree.add_file(f);
			}

			if (f.isDirectory()) {
				search_directory(tree, f);
			}
		}

	}

	//ヘッダー部
	private byte[] header_field(String package_name, String package_version, int directory_count, int file_count) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write("RAMPACK".getBytes());
		baos.write(0x00);

		//ディレクトリ数
		ByteBuffer directory_count_buffer = ByteBuffer.allocate(4);
		directory_count_buffer.order(ByteOrder.BIG_ENDIAN);
		directory_count_buffer.putInt(directory_count);
		baos.write(directory_count_buffer.array());

		//ファイル数
		ByteBuffer file_count_buffer = ByteBuffer.allocate(4);
		file_count_buffer.order(ByteOrder.BIG_ENDIAN);
		file_count_buffer.putInt(file_count);
		baos.write(file_count_buffer.array());

		//作成日時
		LocalDateTime now_date = LocalDateTime.now();
		baos.write((now_date.getYear() >> 8) & 0xFF);		//西暦上位バイト
		baos.write(now_date.getYear() & 0xFF);				//西暦下位バイト
		baos.write(now_date.getMonthValue());				//月
		baos.write(now_date.getDayOfMonth());				//日
		baos.write(now_date.getHour());						//時
		baos.write(now_date.getMinute());					//分
		baos.write(now_date.getSecond());					//秒
		baos.write(0x00);										//予約済み

		//パッケージ名
		byte[] package_name_byte = package_name.getBytes();
		baos.write(package_name_byte.length);
		baos.write(package_name_byte);

		//パッケージバージョン
		byte[] package_version_byte = package_version.getBytes();
		baos.write(package_version_byte.length);
		baos.write(package_version_byte);

		return baos.toByteArray();
	}

	private byte[] directory_field(File d) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//パス
		byte[] path_byte = d.getPath().replaceFirst(base_path, "").getBytes();
		baos.write(path_byte.length);
		baos.write(path_byte);

		//権限
		baos.write(0x07);
		baos.write(0x07);
		baos.write(0x07);

		return baos.toByteArray();
	}

	private byte[] file_field(File f) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		//パス
		byte[] path_byte = f.getPath().replaceFirst(base_path, "").getBytes();
		baos.write(path_byte.length);
		baos.write(path_byte);

		//権限
		baos.write(0x07);
		baos.write(0x07);
		baos.write(0x07);

		//ファイルのデータサイズ
		long size = f.length();
		ByteBuffer size_buffer = ByteBuffer.allocate(8);
		size_buffer.order(ByteOrder.BIG_ENDIAN);
		size_buffer.putLong(size);
		baos.write(size_buffer.array());

		FileInputStream fis = new FileInputStream(f);
		byte[] data_buffer = new byte[1024];
		int read_length;
		while ((read_length = fis.read(data_buffer)) != -1) {
			baos.write(data_buffer, 0, read_length);
		}
		fis.close();

		return baos.toByteArray();
	}
}
