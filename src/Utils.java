import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Utils {
	
	public static String[] splitAndTrim(String toSplit, String delimiter) {
		String[] splitted = toSplit.split(delimiter, -1);
		String[] toReturn = new String[splitted.length];

		for (int i = 0; i < splitted.length; i++) {
			toReturn[i] = splitted[i].trim();
		}

		return toReturn;
	}
	
	public static BufferedWriter newWriter(String path, String name, String type) throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + name + type)));
	}

	public static String runQuery(String query) {
        //query the sql databse with the string argument

	    String answer = "";

	    return answer;
	}

	public static String retrievePredefinedQuery(int number) {
	    //retrieve the query, which are all stored on the line corresponding to their number

        //return it
        return "";
    }

}
