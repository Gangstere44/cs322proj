import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

	private static final String NULL = "NULL";

	// New Entity tables
	private static final String CHARACTER = "character";
	private static final String ARTIST = "artist";
	private static final String EDITOR = "editor";
	private static final String GENRE = "genre";

	// Relational tables (so files) needed for Story
	private static final String STORY_TO_FEATURE = "story_to_feature";
	private static final String STORY_TO_SCRIPT = "story_to_script";
	private static final String STORY_TO_PENCILS = "story_to_pencils";
	private static final String STORY_TO_INKS = "story_to_inks";
	private static final String STORY_TO_COLORS = "story_to_colors";
	private static final String STORY_TO_LETTERS = "story_to_letters";
	private static final String STORY_TO_EDITING = "story_to_editing";
	private static final String STORY_TO_GENRE = "story_to_genre";
	private static final String STORY_TO_CHARACTERS = "story_to_characters";

	// Relational tables (so files) needed for Issue
	private static final String ISSUE_IN_SERIES = "issue_in_series";
	private static final String ISSUE_TO_EDITING = "issue_to_editing";

	// New simple relations needed
	private final Map<String, Integer> characters = new HashMap<>();
	private final Map<String, Integer> artists = new HashMap<>();
	private final Map<String, Integer> editors = new HashMap<>();
	private final Map<String, Integer> genres = new HashMap<>();

	// Class attributes
	private final String pathToParse;
	private final String parsedPath;
	private final String fileType;

	public Parser(String pathToParse, String parsedPath, String fileType) throws FileNotFoundException {
		this.pathToParse = pathToParse;
		this.parsedPath = parsedPath;
		this.fileType = fileType;
		
		File inFolder = new File(pathToParse);
		if (!inFolder.exists()) {
			throw new FileNotFoundException("The folder '" + pathToParse
					+ "' was not found. It should be outside your 'src' directory.");
		}
		
		File outFolder = new File(parsedPath);
		if (!outFolder.exists()) {
			throw new FileNotFoundException("The folder '" + parsedPath
					+ "' was not found. It should be outside your 'src' directory.");
		}
	}

	public void parse() throws IOException {
		File folder = new File(this.pathToParse);
		File[] filesToParse = folder.listFiles();

		if (filesToParse == null || filesToParse.length == 0) {
			System.out.println("[WARNING] Parsing 0 files. Check that you indeed have the directory '"
					+ this.pathToParse + "' with the files to parse right outside your 'src' directory.");
		} else if (filesToParse.length != Main.EXPECTED_NB_FILES_TO_PARSE) {
			System.out.println("[WARNING] Parsing " + filesToParse.length + " files, but was expecting to parse "
					+ Main.EXPECTED_NB_FILES_TO_PARSE + ".");
		}

		for (File file : filesToParse) {

			String name = file.getName().split("\\.")[0];

			if (file.getName().split("\\.").length > 1 && !("." + file.getName().split("\\.")[1]).equals(this.fileType)) {
				System.out.println("[WARNING] Trying to parse '" + file.getName() + "', but expected type to be '"
						+ this.fileType + "'.");
			}

			// Create new Map containing that will contain all the new Relations
			// info
			// with each corresponding header
			Map<String, NewRelationInfo> headersToNewRelations = new HashMap<>();

			switch (name) {

			case "issue":
				headersToNewRelations.put("series_id",
						new NewRelationInfo(null, newWriter(ISSUE_IN_SERIES), "issue_id,series_id"));
				headersToNewRelations.put("editing",
						new NewRelationInfo(this.editors, newWriter(ISSUE_TO_EDITING), "issue_id,editor_id"));
				break;

			case "story":
				headersToNewRelations.put("feature",
						new NewRelationInfo(this.characters, newWriter(STORY_TO_FEATURE), "story_id,character_id"));
				headersToNewRelations.put("script",
						new NewRelationInfo(this.artists, newWriter(STORY_TO_SCRIPT), "story_id,artist_id"));
				headersToNewRelations.put("pencils",
						new NewRelationInfo(this.artists, newWriter(STORY_TO_PENCILS), "story_id,artist_id"));
				headersToNewRelations.put("inks",
						new NewRelationInfo(this.artists, newWriter(STORY_TO_INKS), "story_id,artist_id"));
				headersToNewRelations.put("colors",
						new NewRelationInfo(this.artists, newWriter(STORY_TO_COLORS), "story_id,artist_id"));
				headersToNewRelations.put("letters",
						new NewRelationInfo(this.artists, newWriter(STORY_TO_LETTERS), "story_id,artist_id"));
				headersToNewRelations.put("editing",
						new NewRelationInfo(this.editors, newWriter(STORY_TO_EDITING), "story_id,editor_id"));
				headersToNewRelations.put("genre",
						new NewRelationInfo(this.genres, newWriter(STORY_TO_GENRE), "story_id,genre_id"));
				headersToNewRelations.put("characters",
						new NewRelationInfo(this.characters, newWriter(STORY_TO_CHARACTERS), "story_id,character_id"));
				break;

			default:
				break;
			}

			// Will close the writers
			parseAndCreateNewRelations(name, headersToNewRelations);

		}

		// Write new entity tables
		writeEntityTable(this.characters, CHARACTER);
		writeEntityTable(this.artists, ARTIST);
		writeEntityTable(this.editors, EDITOR);
		writeEntityTable(this.genres, GENRE);
	}

	private void parseAndCreateNewRelations(String name, Map<String, NewRelationInfo> headersToNewRelations)
			throws IOException {

		// Write headers for new relations
		for (NewRelationInfo newRelationInfo : headersToNewRelations.values()) {
			newRelationInfo.writer.write(newRelationInfo.newRelationHeader + '\n');
		}

		// Create main writer (the one writing the same file than the one read,
		// but in parsed directory)
		BufferedWriter mainWriter = newWriter(name);

		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(this.pathToParse + name + this.fileType)))) {
			boolean isHeader = true;
			List<String> headers = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] allData = Utils.splitAndTrim(line, ",");
				if (isHeader) {
					// Header
					headers = Arrays.asList(allData);
					String delimiter = "";
					for (String data : allData) {
						if (!headersToNewRelations.keySet().contains(data)) {
							mainWriter.write(delimiter + data);
							delimiter = ",";
						}
					}
					mainWriter.write('\n');
					isHeader = false;
				} else {
					// Normal data
					String delimiter = "";
					Integer id = null;
					for (int i = 0; i < allData.length; i++) {
						String header = headers.get(i);
						NewRelationInfo newRelationInfo = headersToNewRelations.get(header);
						String data = allData[i];
						if (data.length() == 0 && newRelationInfo == null) {
							mainWriter.write(delimiter + NULL);
							delimiter = ",";
						} else if (data.length() > 0) {
							if (newRelationInfo == null) {
								// Normal column
								mainWriter.write(delimiter + data);
								delimiter = ",";
								if (header.equals("id")) {
									id = Integer.parseInt(data);
								}
							} else {
								// Column that will collapse and create a new
								// Relation
								newRelationInfo.addNewElements(data, id);
							}
						}
					}
					mainWriter.write('\n');
				}
			}
		}

		// Close writers
		mainWriter.close();
		for (NewRelationInfo newRelationInfo : headersToNewRelations.values()) {
			newRelationInfo.writer.close();
		}
	}

	private BufferedWriter newWriter(String name) throws FileNotFoundException {
		return Utils.newWriter(this.parsedPath, name, this.fileType);
	}

	private void writeEntityTable(Map<String, Integer> entity, String name) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + name + this.fileType)));

		writer.write("id,name\n");

		for (Map.Entry<String, Integer> entry : entity.entrySet()) {
			writer.write(entry.getValue() + "," + entry.getKey() + '\n');
		}

		writer.close();
	}

	// Store information about a new relation that needs to be created
	static class NewRelationInfo {

		public Map<String, Integer> elemToId;
		public BufferedWriter writer;
		public String newRelationHeader;

		public NewRelationInfo(Map<String, Integer> elemToId, BufferedWriter writer, String newRelationHeader) {
			this.elemToId = elemToId;
			this.writer = writer;
			this.newRelationHeader = newRelationHeader;
		}

		// Add some elements (if new) to a relation and write it
		public void addNewElements(String data, int id) throws IOException {
			String[] elements = Utils.splitAndTrim(data, ";");
			for (String elem : elements) {
				if (elem.length() > 0) {
					Integer relationId;
					// If no Map was provided, elem is already the relationId
					// (like for IssueInSeries)
					if (elemToId == null) {
						relationId = Integer.parseInt(elem);
					} else {
						relationId = elemToId.get(elem);
						if (relationId == null) {
							relationId = elemToId.size();
							elemToId.put(elem, relationId);
						}
					}
					writer.write(id + "," + relationId + '\n');
				}
			}
		}

	}

}
