import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		// Don't use StringBuilder to avoid OutOfMemoryError (Java heap space too small sometimes)
		BufferedWriter mainWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + name + this.fileType)));
		BufferedWriter storyToFeatureWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_FEATURE + this.fileType)));
		BufferedWriter storyToScriptWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_SCRIPT + this.fileType)));
		BufferedWriter storyToPencilsWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_PENCILS + this.fileType)));
		BufferedWriter storyToInksWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_INKS + this.fileType)));
		BufferedWriter storyToColorsWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_COLORS + this.fileType)));
		BufferedWriter storyToLettersWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_LETTERS + this.fileType)));
		BufferedWriter storyToEditingWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_EDITING + this.fileType)));
		BufferedWriter storyToGenreWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_GENRE + this.fileType)));
		BufferedWriter storyToCharactersWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.parsedPath + STORY_TO_CHARACTERS + this.fileType)));

		// Add headers to relational StringBuilder
		storyToFeatureWriter.write("story_id,character_id\n");
		storyToScriptWriter.write("story_id,artist_id\n");
		storyToPencilsWriter.write("story_id,artist_id\n");
		storyToInksWriter.write("story_id,artist_id\n");
		storyToColorsWriter.write("story_id,artist_id\n");
		storyToLettersWriter.write("story_id,artist_id\n");
		storyToEditingWriter.write("story_id,editor_id\n");
		storyToGenreWriter.write("story_id,genre_id\n");
		storyToCharactersWriter.write("story_id,character_id\n");

		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(this.pathToParse + name + this.fileType)))) {
			boolean header = true;
			List<String> headers = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] allData = splitAndClean(line, ",");
				if (header) {
					headers = Arrays.asList(allData);
					mainWriter.write(line + '\n');
					header = false;
				}
				// Normal data
				else {
					String delimiter = "";
					int storyId = -1;
					for (int i = 0; i < allData.length; i++) {
						String data = allData[i];
						if (data.length() == 0) {
							mainWriter.write(delimiter + NULL);
						}
						else {
							switch (headers.get(i)) {
								case "id":
									storyId = Integer.parseInt(data);
									mainWriter.write(delimiter + data);
									break;
								case "feature":
									addNewElementsToRelation(data, this.characters, storyId, storyToFeatureWriter);
									break;
								case "script":
									addNewElementsToRelation(data, this.artists, storyId, storyToScriptWriter);
									break;
								case "pencils":
									addNewElementsToRelation(data, this.artists, storyId, storyToPencilsWriter);
									break;
								case "inks":
									addNewElementsToRelation(data, this.artists, storyId, storyToInksWriter);
									break;
								case "colors":
									addNewElementsToRelation(data, this.artists, storyId, storyToColorsWriter);
									break;
								case "letters":
									addNewElementsToRelation(data, this.artists, storyId, storyToLettersWriter);
									break;
								case "editing":
									addNewElementsToRelation(data, this.editors, storyId, storyToEditingWriter);
									break;
								case "genre":
									addNewElementsToRelation(data, this.genres, storyId, storyToGenreWriter);
									break;
								case "characters":
									addNewElementsToRelation(data, this.characters, storyId, storyToCharactersWriter);
									break;
								default:
									mainWriter.write(delimiter + data);
									break;
							}
						}
						delimiter = ",";
					}
					mainWriter.write('\n');
				}
			}
		}
		
		/*
		// Write to all files what we stored in their StringBuilder
		writeToFile(this.parsedPath + name + this.fileType, mainStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_FEATURE + this.fileType, storyToFeatureStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_SCRIPT + this.fileType, storyToScriptStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_PENCILS + this.fileType, storyToPencilsStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_INKS + this.fileType, storyToInksStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_COLORS + this.fileType, storyToColorsStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_LETTERS + this.fileType, storyToLettersStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_EDITING + this.fileType, storyToEditingStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_GENRE + this.fileType, storyToGenreStrB.toString());
		writeToFile(this.parsedPath + STORY_TO_CHARACTERS + this.fileType, storyToCharactersStrB.toString());
		*/
	}
	
	private void parseDefault(String name) throws IOException {

		// Use StringBuilder to avoid writing too many times
		StringBuilder strB = new StringBuilder();

		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(this.pathToParse + name + this.fileType)))) {
			boolean header = true;
			String line;
			while ((line = br.readLine()) != null) {

				if (header) {
					strB.append(line + '\n');
					header = false;
				}
				// Normal data
				else {
					String[] allData = splitAndClean(line, ",");
					String delimiter = "";
					for (String data : allData) {
						if (data.length() == 0) {
							data = NULL;
						}
						strB.append(delimiter + data);
						delimiter = ",";
					}
					strB.append('\n');
				}
			}
		}

		writeToFile(this.parsedPath + name + this.fileType, strB.toString());
		
	}
	
	private void writeToFile(String fileName, String toWrite) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileName)));

		writer.write(toWrite);

		writer.close();
		
		System.out.println("Wrote file: '" + fileName + "'.");
	}

	// Add some elements (if new) to a relation an put it in the given StringBuilder
	private void addNewElementsToRelation(String data, Map<String, Integer> relation, int id, BufferedWriter writer) throws IOException {
		String[] elements = splitAndClean(data, ";");
		for (String elem : elements) {
			Integer relationId = relation.get(elem);
			if (relationId == null) {
				relationId = relation.size();
				relation.put(elem, relationId);
			}
			writer.write(id + "," + relationId + '\n');
		}
	}

	private String[] splitAndClean(String toSplit, String delimiter) {
		String[] splitted = toSplit.split(delimiter);
		String[] toReturn = new String[splitted.length];
		
		for (int i = 0; i < splitted.length; i++) {
			toReturn[i] = splitted[i].trim();
		}
		
		return toReturn;
	}

}
