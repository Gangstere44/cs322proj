import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	public static void rewriteRelationFile(File file) throws FileNotFoundException, IOException {
		Map<String, Set<String>> map = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(
				new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] splittedLine = Utils.splitAndTrim(line, ",");
				assert(splittedLine.length == 2);
				String key = splittedLine[0];
				String value = splittedLine[1];
				Set<String> set = map.get(key);
				if (set == null) {
					set = new HashSet<>();
					map.put(key, set);
				}
				set.add(value);
			}
		}
		
		File newFile = new File(file.getParent() + "/new_" + file.getName());
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile)));
		
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			for (String value : entry.getValue()) {
				writer.write(entry.getKey() + "," + value + "\n");
			}
		}
		
		writer.close();
		
		System.out.println("New file at: " + newFile.getName());
	}
}
