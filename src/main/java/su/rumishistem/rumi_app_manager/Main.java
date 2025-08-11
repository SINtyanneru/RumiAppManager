package su.rumishistem.rumi_app_manager;

import java.io.File;
import java.io.IOException;

import su.rumishistem.rumi_app_manager.RamPackage.Packer;
import su.rumishistem.rumi_app_manager.RamPackage.Unpacker;

public class Main {
	public static void main(String[] args) throws IOException {
		new Packer().pack("test", "1.0", new File("/home/rumisan/Documents/test_pkg"));

		new Unpacker(new File("test.rpk")).show_info();
	}
}
