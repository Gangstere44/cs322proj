import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Utils {
	
	public static String[] splitAndClean(String toSplit, String delimiter) {
		String[] splitted = toSplit.split(delimiter);
		String[] toReturn = new String[splitted.length];

		for (int i = 0; i < splitted.length; i++) {
			toReturn[i] = splitted[i].trim();
		}

		return toReturn;
	}
	
	public static BufferedWriter newWriter(String path, String name, String type) throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + name + type)));
	}

}
