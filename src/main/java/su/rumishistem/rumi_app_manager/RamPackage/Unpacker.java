package su.rumishistem.rumi_app_manager.RamPackage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;

public class Unpacker {
	private static final byte[] SIGNATURE = new byte[] {'R', 'A', 'M', 'P', 'A', 'C', 'K', 0x00};

	private File package_file;
	private RandomAccessFile raf;

	public Unpacker(File package_file) throws IOException {
		if (!package_file.exists()) throw new RuntimeException("ファイルがありません。");
		if (!package_file.isFile()) throw new RuntimeException("ファイルではありません。");

		this.package_file = package_file;

		try {
			//これでエラー出るのはおかしい
			this.raf = new RandomAccessFile(package_file, "r");
		} catch (Exception EX) {
			EX.printStackTrace();
		}

		if (!check_signature()) {
			throw new RuntimeException("シグネチャが一致しません。");
		}
	}

	//情報を表示する
	public void show_info() throws IOException {
		int directory_count = get_directory_count();
		int file_count = get_file_count();
		LocalDateTime date = get_date();
		String name = get_name();
		String version = get_version();

		System.out.println("パッケージ名		：" + name);
		System.out.println("バージョン		：" + version);
		System.out.println("パッケージ作成日時	：" + date.getYear()+"年"+date.getMonthValue()+"月"+date.getDayOfMonth()+"日 "+date.getHour()+"時"+date.getMinute()+"分"+date.getSecond()+"秒");

		//ディレクトリを読む
		System.out.println("ディレクトリ数		：" + directory_count);
		for (int i = 0; i < directory_count; i++) {
			//パス
			int length = raf.readUnsignedByte();
			byte[] path_byte = new byte[length];
			raf.readFully(path_byte);
			String path = new String(path_byte, "UTF-8");

			//権限
			int permission1 = raf.readUnsignedByte();
			int permission2 = raf.readUnsignedByte();
			int permission3 = raf.readUnsignedByte();

			System.out.println("> " + permission1+permission2+permission3 + "			" + path);
		}

		//ファイルを読む
		System.out.println("ファイル数		：" + file_count);
		for (int i = 0; i < file_count; i++) {
			//パス
			int length = raf.readUnsignedByte();
			byte[] path_byte = new byte[length];
			raf.readFully(path_byte);
			String path = new String(path_byte, "UTF-8");

			//権限
			int permission1 = raf.readUnsignedByte();
			int permission2 = raf.readUnsignedByte();
			int permission3 = raf.readUnsignedByte();

			//ファイルサイズ
			long size = raf.readLong();

			System.out.println("> " + permission1+permission2+permission3+"	" + size + "byte		" + path);

			raf.seek(raf.getFilePointer() + size);
		}
	}

	//シグネチャチェック
	private boolean check_signature() throws IOException {
		//先頭へ移動
		raf.seek(0);

		byte[] buffer = new byte[8];
		int byte_read = raf.read(buffer);

		for (int i = 0; i < SIGNATURE.length; i++) {
			byte s = SIGNATURE[i];
			byte b = buffer[i];

			if (s != b) {
				return false;
			}
		}

		return true;
	}

	//シグネチャの後に移動
	private void seek_signaure_after() throws IOException {
		raf.seek(SIGNATURE.length);
	}

	//ディレクトリの数を取得
	private int get_directory_count() throws IOException {
		seek_signaure_after();

		int b1 = raf.readUnsignedByte();
		int b2 = raf.readUnsignedByte();
		int b3 = raf.readUnsignedByte();
		int b4 = raf.readUnsignedByte();

		int value = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;

		return value;
	}

	//ファイルの数を取得
	private int get_file_count() throws IOException {
		seek_signaure_after();
		raf.seek(raf.getFilePointer() + 4);//←4=ディレクトリ数のバイト数

		int b1 = raf.readUnsignedByte();
		int b2 = raf.readUnsignedByte();
		int b3 = raf.readUnsignedByte();
		int b4 = raf.readUnsignedByte();

		int value = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;

		return value;
	}

	//パッケージングした日付
	private LocalDateTime get_date() throws IOException {
		seek_signaure_after();
		raf.seek(raf.getFilePointer() + 4 + 4);//←4=ディレクトリ/ファイル数のバイト数

		int year = (raf.read() << 8) | raf.read();
		int month = raf.read();
		int day = raf.read();
		int hour = raf.read();
		int min = raf.read();
		int sec = raf.read();

		return LocalDateTime.of(year, month, day, hour, min, sec);
	}

	private String get_name() throws IOException {
		seek_signaure_after();
		raf.seek(raf.getFilePointer() + 4 + 4 + 8);//←4=ディレクトリ/ファイル数 | 8=日付

		//リード
		int length = raf.readUnsignedByte();
		byte[] name_byte = new byte[length];
		raf.readFully(name_byte);

		return new String(name_byte, "UTF-8");
	}

	private String get_version() throws IOException {
		seek_signaure_after();
		raf.seek(raf.getFilePointer() + 4 + 4 + 8);					//←4=ディレクトリ/ファイル数 | 8=日付

		//パッケージ名を読み飛ばす
		int name_length = raf.readUnsignedByte();
		raf.seek(raf.getFilePointer() + name_length);

		//リード
		int length = raf.readUnsignedByte();
		byte[] version_byte = new byte[length];
		raf.readFully(version_byte);

		return new String(version_byte, "UTF-8");
	}
}
