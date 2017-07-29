package package1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashSet;

final class ManageTickerList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4244506744475288117L;

	static void saveTickerList() throws FileNotFoundException, IOException, URISyntaxException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(MakeDir.containerPath));
			oos.writeObject(GUI.tickerList);
		} finally {
			try {
				if (null != oos) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	static HashSet<String> loadTickerList() throws FileNotFoundException, IOException, ClassNotFoundException, URISyntaxException {
		HashSet<String> set = new HashSet<String>();
		ObjectInputStream ois = null;
		try {
			File file = new File(MakeDir.containerPath);
			if (file.length() != 0) {
				ois = new ObjectInputStream(new FileInputStream(file));
				set = (HashSet<String>) ois.readObject();
			}
		} finally {
			try {
				if (null != ois) {
					ois.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return set;
	}

}
