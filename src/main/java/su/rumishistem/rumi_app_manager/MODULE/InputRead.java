package su.rumishistem.rumi_app_manager.MODULE;

import java.util.Scanner;

public class InputRead {
	public static String Read() {
		Scanner SCAN = new Scanner(System.in);
		String RESULT = SCAN.next();
		SCAN.close();
		return RESULT;
	}
}
