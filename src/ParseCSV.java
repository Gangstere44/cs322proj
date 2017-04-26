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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseCSV {

	private static final String NULL = "NULL";

	// New Entity tables
	private static final String CHARACTERS = "characters";
	private static final String ARTISTS = "artists";
	private static final String EDITORS = "editors";
	private static final String GENRES = "genres";

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

	private final String pathToParse;
	private final String parsedPath;
	private final String fileType;

	// New simple relations needed
	private final Map<String, Integer> characters = new HashMap<>();
	private final Map<String, Integer> artists = new HashMap<>();
	private final Map<String, Integer> editors = new HashMap<>();
	private final Map<String, Integer> genres = new HashMap<>();

	public ParseCSV(String pathToParse, String parsedPath, String fileType) {
		this.pathToParse = pathToParse;
		this.parsedPath = parsedPath;
		this.fileType = fileType;
	}

	public void parseCSV() throws IOException {
		File folder = new File(this.pathToParse);
		File[] filesToParse = folder.listFiles();

		for (File file : filesToParse) {
			String name = file.getName().split("\\.")[0];
			switch (name) {
			case "brand_group":
				break;
			case "indicia_publisher":
				break;
			case "issue":
				break;
			case "issue_reprint":
				break;
			case "language":
				break;
			case "publisher":
				break;
			case "series":
				break;
			case "series_publication_type":
				break;
			case "story":
				parseStory(name);
				break;
			case "story_reprint":
				break;
			case "story_type":
				break;
			default:
				parseDefault(name);
				break;
			}
		}

		// Write new entity tables
		writeEntityTable(this.characters, CHARACTERS);
		writeEntityTable(this.artists, ARTISTS);
		writeEntityTable(this.editors, EDITORS);
		writeEntityTable(this.genres, GENRES);
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

	private void parseStory(String name) throws IOException {

		// Store which columns will create a new Relation (table) and thus will
		// be removed from the "Story" one
		Set<String> newRelationColumns = new HashSet<>(Arrays.asList(new String[] { "feature", "script", "pencils",
				"inks", "colors", "letters", "editing", "genre", "characters" }));

		/*
		 * Store all information about the new Relations that are about to be
		 * created
		 */

		// Don't use StringBuilder to avoid OutOfMemoryError (Java heap space
		// too small sometimes), directly use BufferedReader instead

		Map<String, NewRelationInfo> headersToNewRelations = new HashMap<>();

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

		// Write headers for new relations
		for (NewRelationInfo newRelationInfo : headersToNewRelations.values()) {
			newRelationInfo.writer.write(newRelationInfo.newRelationHeader + '\n');
		}
		
		// Create main writer (the one writing the same file than the one read, but in parsed directory)
		BufferedWriter mainWriter = newWriter(name);

		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(this.pathToParse + name + this.fileType)))) {
			boolean isHeader = true;
			List<String> headers = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] allData = Utils.splitAndClean(line, ",");
				if (isHeader) {
					// Header
					headers = Arrays.asList(allData);
					String delimiter = "";
					for (String data : allData) {
						if (!newRelationColumns.contains(data)) {
							mainWriter.write(delimiter + data);
							delimiter = ",";
						}
					}
					mainWriter.write('\n');
					isHeader = false;
				}
				else {
					// Normal data
					String delimiter = "";
					int storyId = -1;
					for (int i = 0; i < allData.length; i++) {
						String header = headers.get(i);
						NewRelationInfo newRelationInfo = headersToNewRelations.get(header);
						String data = allData[i];
						if (data.length() == 0 && newRelationInfo == null) {
							mainWriter.write(delimiter + NULL);
							delimiter = ",";
						} 
						else if (data.length() > 0) {
							if (newRelationInfo == null) {
								// Normal column
								mainWriter.write(delimiter + data);
								delimiter = ",";
								if (header.equals("id")) {
									storyId = Integer.parseInt(data);
								}
							}
							else {
								// Column that will collapse and create a new Relation
								newRelationInfo.addNewElements(data, storyId);
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

	private void parseDefault(String name) throws IOException {

		// Use StringBuilder to avoid writing too many times
		StringBuilder strB = new StringBuilder();
		BufferedWriter writer = newWriter(name);

		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(this.pathToParse + name + this.fileType)))) {
			boolean header = true;
			String line;
			while ((line = br.readLine()) != null) {

				if (header) {
					writer.write(line + '\n');
					header = false;
				}
				// Normal data
				else {
					String[] allData = Utils.splitAndClean(line, ",");
					String delimiter = "";
					for (String data : allData) {
						if (data.length() == 0) {
							data = NULL;
						}
						writer.write(delimiter + data);
						delimiter = ",";
					}
					writer.write('\n');
				}
			}
		}

		writer.close();

	}

	private BufferedWriter newWriter(String name) throws FileNotFoundException {
		return Utils.newWriter(this.parsedPath, name, this.fileType);
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
			String[] elements = Utils.splitAndClean(data, ";");
			for (String elem : elements) {
				if (elem.length() > 0) {
					Integer relationId = elemToId.get(elem);
					if (relationId == null) {
						relationId = elemToId.size();
						elemToId.put(elem, relationId);
					}
					writer.write(id + "," + relationId + '\n');
				}
			}
		}

	}

}
