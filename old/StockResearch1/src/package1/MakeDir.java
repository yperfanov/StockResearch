package package1;

import java.io.File;
import java.io.IOException;

public class MakeDir {
	
	public static String containerPath;
	public static String stockResearchPath;
	public static String listPath;
	public static String manualPath;
	
	public void makeDir() throws IOException {
		
		File homeDir = new File(System.getProperty("user.home"), ".stockResearch");
		stockResearchPath = homeDir.getAbsolutePath();
		File container = new File(stockResearchPath, "tickerList.save");
		File list = new File(stockResearchPath, "list.txt");
		File manual = new File(stockResearchPath, "manual.txt");
		listPath = list.getAbsolutePath();
		manualPath = manual.getAbsolutePath();
		containerPath = container.getAbsolutePath();
		if (!homeDir.exists()) {
			homeDir.mkdir();
			container.createNewFile();
			list.createNewFile();
			manual.createNewFile();
		}
	}
	
	public static void main(String...strings) throws IOException {
		MakeDir m = new MakeDir();
		m.makeDir();
	}
}
