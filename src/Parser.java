import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
	
	enum Type {
		INT, REAL, VARCHAR;
		
		private String toStr;

	    static {
	        INT.toStr = "NUMBER";
	        REAL.toStr = "NUMBER";
	        VARCHAR.toStr = "VARCHAR";
	    }
	    
	    public String toSQLString() {
	        return toStr;
	    }
	}

	private static final int CURRENT_YEAR = 2017;
	private static final String NULL = "NULL";
	private static final Integer DEFAULT_VARCHAR_SIZE = 100;
	// Default number of digits for decimals
	private static final int DEFAULT_DECIMAL_SCALE = 2;

	// New Entity tables
	private static final String CHARACTERS = "characters";
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
	private final Connection conn;
	
	public Parser(String pathToParse, String parsedPath, String fileType, Connection conn) throws FileNotFoundException {
		this.pathToParse = pathToParse;
		this.parsedPath = parsedPath;
		this.fileType = fileType;
		this.conn = conn;
		
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

	public void parse() throws IOException, SQLException {
		// filesToParse should be in correct order
		String[] fileNames = {"story_type", "language", "country", "series_publication_type", "publisher", "brand_group", 
				"indicia_publisher", "issue-tmp", "issue_reprint", "series-tmp", "story", "story_reprint"};
		
		File[] filesToParse = new File[fileNames.length];
		
		for (int i = 0; i < fileNames.length; i++) {
			filesToParse[i] = new File(this.pathToParse + fileNames[i] + this.fileType);
		}
		
		if (filesToParse == null || filesToParse.length == 0) {
			System.out.println("[WARNING] Parsing 0 files. Check that you indeed have the directory '"
					+ this.pathToParse + "' with the files to parse right outside your 'src' directory.");
		} else if (filesToParse.length != Main.EXPECTED_NB_FILES_TO_PARSE) {
			System.out.println("[WARNING] Parsing " + filesToParse.length + " files, but was expecting to parse "
					+ Main.EXPECTED_NB_FILES_TO_PARSE + ".");
		}
		
		// Firstly we need to change the column publication_dates into 2 columns: publication_date_from_year
		// and publication_date_to_year, which will contain an Integer being the corresponding year
		
		File series = new File(this.pathToParse + "series" + this.fileType);
		File tmp = new File(this.pathToParse + "series-tmp" + this.fileType);
		tmp.delete();
		
		BufferedWriter newSeriesWriter = Utils.newWriter(this.pathToParse, "series-tmp", this.fileType);
		
		try (BufferedReader br = new BufferedReader(
				new FileReader(series))) {
			boolean isHeader = true;
			List<String> headers = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] allData = Utils.splitAndTrim(line, ",");
				String delimiter = "";
				if (isHeader) {
					isHeader = false;
					for (String data : allData) {
						headers.add(data);
						if (data.equals("publication_dates")) {
							newSeriesWriter.write(delimiter + "publication_dates_from_year,publication_dates_to_year");
						}
						else {
							newSeriesWriter.write(delimiter + data);
						}
						delimiter = ",";
					}
					newSeriesWriter.write('\n');
				}
				else {
					for (int i = 0; i < allData.length; i++) {
						String header = headers.get(i);
						String data = allData[i];
						if (data.length() == 0) {
							newSeriesWriter.write(delimiter);
							delimiter = ",";
							if (header.equals("publication_dates")) {
								newSeriesWriter.write(delimiter);
							}
						}
						else {
							if (header.equals("publication_dates")) {
								// Need to smartly split and get the from and to years
								
								// First replace all occurrences of "Presente", "Present", "presente" or "present" by the current year
								data = data.replace("Presente", CURRENT_YEAR+"");
								data = data.replace("Present", CURRENT_YEAR+"");
								data = data.replace("presente", CURRENT_YEAR+"");
								data = data.replace("present", CURRENT_YEAR+"");
								
								// Replace the "to"s by '-'
								data = data.replace(" to ", "-");
								
								// Now keep only the '-' and numbers
								data = data.replaceAll("[^0-9-]", "");
								if (data.startsWith("-")) {
									data = data.substring(1);
								}
								
								// Try to find something when splitting by '-'
								String[] splittedByDash = data.split("-+");
								String fromYear = NULL;
								String toYear = NULL;
								if (splittedByDash.length == 1) {
									try {
										fromYear = toYear = Integer.parseInt(data)+"";
									}
									catch (NumberFormatException e) {
										// Do nothing, we will try something else later maybe
									}
								}
								else if (splittedByDash.length == 2) {
									try {
										fromYear = Integer.parseInt(splittedByDash[0])+"";
										toYear = Integer.parseInt(splittedByDash[1])+"";
									}
									catch (NumberFormatException e) {
										// Do nothing, we will try something else later maybe
									}
								}
								if (fromYear.length() != 4) {
									fromYear = NULL;
								}
								if (toYear.length() != 4) {
									toYear = NULL;
								}
								newSeriesWriter.write(delimiter + fromYear + "," + toYear);
							}
							else {
								newSeriesWriter.write(delimiter + data);
							}
						}
						delimiter = ",";
					}
					newSeriesWriter.write('\n');
				}
			}
		}
		
		// Secondly, we need to change the columns supposed to be a date in the file 'issue'
		// into Integers representing the year (easier than playing with dates)
		// + change column 'number' to 'num'
		// + change price into number only (0.05 USD -> 0.05)
		
		File issue = new File(this.pathToParse + "issue" + this.fileType);
		tmp = new File(this.pathToParse + "issue-tmp" + this.fileType);
		tmp.delete();
		
		BufferedWriter newIssueWriter = Utils.newWriter(this.pathToParse, "issue-tmp", this.fileType);
		
		try (BufferedReader br = new BufferedReader(
				new FileReader(issue))) {
			boolean isHeader = true;
			List<String> headers = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] allData = Utils.splitAndTrim(line, ",");
				String delimiter = "";
				if (isHeader) {
					isHeader = false;
					for (String data : allData) {
						headers.add(data);
						if (data.equals("number")) {
							newIssueWriter.write(delimiter + "num");
						}
						else {
							newIssueWriter.write(delimiter + data);
						}
						delimiter = ",";
					}
					newIssueWriter.write('\n');
				}
				else {
					for (int i = 0; i < allData.length; i++) {
						String header = headers.get(i);
						String data = allData[i];
						if (data.length() == 0) {
							newIssueWriter.write(delimiter);
						}
						else {
							if (header.equals("publication_date") || header.equals("on_sale_date")) {
								// Need to smartly get year only
								
								// Keep only the numbers
								data = data.replaceAll("[^0-9]", "");
								
								String year = NULL;
								try {
									year = Integer.parseInt(data)+"";
								}
								catch (NumberFormatException e) {
									// Do nothing, will be null
								}
								if (year.length() != 4) {
									year = NULL;
								}
								
								newIssueWriter.write(delimiter + year);
							}
							else if (header.equals("price")) {
								String[] splittedData = data.split("\\s+");
								if (splittedData.length > 0) {
									newIssueWriter.write(delimiter + splittedData[0]);
								}
								else {
									newIssueWriter.write(delimiter + data);
								}
							}
							else {
								newIssueWriter.write(delimiter + data);
							}
						}
						delimiter = ",";
					}
					newIssueWriter.write('\n');
				}
			}
		}
		
		for (File file : filesToParse) {

			String name = file.getName().split("\\.")[0];

			if (file.getName().split("\\.").length > 1 && !("." + file.getName().split("\\.")[1]).equals(this.fileType)) {
				System.out.println("[WARNING] Trying to parse '" + file.getName() + "', but expected type to be '"
						+ this.fileType + "'.");
			}

			// Create new Map that will contain all the new Relations
			// info with each corresponding header
			Map<String, NewRelationInfo> headersToNewRelations = new HashMap<>();
			
			// Create new Map that will contain all the column info
			// with each corresponding header
			Map<String, ColumnInfo> headersToColumnInfo = new HashMap<>();
			
			// List of references (if any)
			List<Reference> references = new ArrayList<>();
			
			// Primary key (by default is 'id')
			String primary = "id";

			switch (name) {

			case "brand_group":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("year_began", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("year_ended", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("url", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("publisher_id", new ColumnInfo(Type.INT, null, false));
				
				references.add(new Reference("publisher_id", "publisher", "id"));
				
				break;
				
			case "country":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("code", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				
				break;
				
			case "indicia_publisher":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("publisher_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("country_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("year_began", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("year_ended", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("is_surrogate", new ColumnInfo(Type.VARCHAR, 1, true));
				headersToColumnInfo.put("notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("url", new ColumnInfo(Type.VARCHAR, 1000, true));
				
				references.add(new Reference("publisher_id", "publisher", "id"));
				references.add(new Reference("country_id", "country", "id"));
				
				break;
				
			case "issue_reprint":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("origin_issue_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("target_issue_id", new ColumnInfo(Type.INT, null, false));
				
				references.add(new Reference("origin_issue_id", "issue", "id"));
				references.add(new Reference("target_issue_id", "issue", "id"));
				
				break;
				
			case "issue-tmp":
				headersToNewRelations.put("series_id",
						new NewRelationInfo(null, newWriter(ISSUE_IN_SERIES), "issue_id,series_id"));
				headersToNewRelations.put("editing",
						new NewRelationInfo(this.editors, newWriter(ISSUE_TO_EDITING), "issue_id,editor_id"));
				
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("num", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("indicia_publisher_id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("publication_date", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("price", new ColumnInfo(Type.REAL, null, true));
				headersToColumnInfo.put("page_count", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("indicia_frequency", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("isbn", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("valid_isbn", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("barcode", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("title", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("on_sale_date", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("rating", new ColumnInfo(Type.REAL, null, true));
				// Special ones
				headersToColumnInfo.put("editing", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				
				references.add(new Reference("indicia_publisher_id", "indicia_publisher", "id"));
				
				break;
				
			case "language":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("code", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));

				break;
				
			case "publisher":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("country_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("year_began", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("year_ended", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("url", new ColumnInfo(Type.VARCHAR, 1000, true));
				
				references.add(new Reference("country_id", "country", "id"));
				
				break;
				
			case "series_publication_type":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				
				break;
				
			case "series-tmp":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("format", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("year_began", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("year_ended", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("publication_dates_from_year", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("publication_dates_to_year", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("first_issue_id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("last_issue_id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("publisher_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("country_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("language_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("color", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("dimensions", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("paper_stock", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("binding", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("publishing_format", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("publication_type_id", new ColumnInfo(Type.INT, null, true));
				
				references.add(new Reference("first_issue_id", "issue", "id"));
				references.add(new Reference("last_issue_id", "issue", "id"));
				references.add(new Reference("publisher_id", "publisher", "id"));
				references.add(new Reference("country_id", "country", "id"));
				references.add(new Reference("language_id", "language", "id"));
				references.add(new Reference("publication_type_id", "series_publication_type", "id"));
				
				break;
				
			case "story_reprint":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("origin_id", new ColumnInfo(Type.INT, null, false));
				headersToColumnInfo.put("target_id", new ColumnInfo(Type.INT, null, false));
				
				references.add(new Reference("origin_id", "story", "id"));
				references.add(new Reference("target_id", "story", "id"));
				
				break;
				
			case "story_type":
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				
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
				
				headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("title", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("issue_id", new ColumnInfo(Type.INT, null, true));
				headersToColumnInfo.put("synopsis", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("reprint_notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("notes", new ColumnInfo(Type.VARCHAR, 1000, true));
				headersToColumnInfo.put("type_id", new ColumnInfo(Type.INT, null, false));
				// Special ones
				headersToColumnInfo.put("feature", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("script", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("pencils", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("inks", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("colors", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("letters", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("editing", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("genre", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				headersToColumnInfo.put("characters", new ColumnInfo(Type.VARCHAR, DEFAULT_VARCHAR_SIZE, true));
				
				references.add(new Reference("issue_id", "issue", "id"));
				references.add(new Reference("type_id", "story_type", "id"));
				
				break;
				
			default:
				break;
			}
			
			// Will close the writers
			parseAndCreateNewRelations(name, headersToNewRelations, headersToColumnInfo);
			
			// Remove useless fields in 'headersToColumnInfo'
			for (String colName : headersToNewRelations.keySet()) {
				headersToColumnInfo.remove(colName);
			}
			
			// Create corresponding table in DB (without inserting of course)
			createTable(createTableQuery(name.split("-")[0], headersToColumnInfo, primary, references));

		}

		// Write new entity tables (should be only valid content)
		writeEntityTable(this.characters, CHARACTERS);
		writeEntityTable(this.artists, ARTIST);
		writeEntityTable(this.editors, EDITOR);
		writeEntityTable(this.genres, GENRE);
		
		// Create new entity tables
		createEntityTable(CHARACTERS, DEFAULT_VARCHAR_SIZE);
		createEntityTable(ARTIST, DEFAULT_VARCHAR_SIZE);
		createEntityTable(EDITOR, DEFAULT_VARCHAR_SIZE);
		createEntityTable(GENRE, DEFAULT_VARCHAR_SIZE);
		
		// Create the tables corresponding to the new relations
		createNewRelationsTables();
		
	}

	private void createNewRelationsTables() throws SQLException {
		Map<String, ColumnInfo> headersToColumnInfo = new HashMap<>();
		List<Reference> references = new ArrayList<>();
		
		// story_to_feature
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("artist_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("artist_id", ARTIST, "id"));
		
		createTable(createTableQuery(STORY_TO_FEATURE, headersToColumnInfo, "story_id,artist_id", references));
		
		// story_to_script
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("artist_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("artist_id", ARTIST, "id"));
		
		createTable(createTableQuery(STORY_TO_SCRIPT, headersToColumnInfo, "story_id,artist_id", references));
		
		// story_to_pencils
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("artist_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("artist_id", ARTIST, "id"));
		
		createTable(createTableQuery(STORY_TO_PENCILS, headersToColumnInfo, "story_id,artist_id", references));
		
		// story_to_inks
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("artist_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("artist_id", ARTIST, "id"));
		
		createTable(createTableQuery(STORY_TO_INKS, headersToColumnInfo, "story_id,artist_id", references));
		
		// story_to_colors
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("artist_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("artist_id", ARTIST, "id"));
		
		createTable(createTableQuery(STORY_TO_COLORS, headersToColumnInfo, "story_id,artist_id", references));
		
		// story_to_letters
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("artist_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("artist_id", ARTIST, "id"));
		
		createTable(createTableQuery(STORY_TO_LETTERS, headersToColumnInfo, "story_id,artist_id", references));
		
		// story_to_editing
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("editor_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("editor_id", EDITOR, "id"));
		
		createTable(createTableQuery(STORY_TO_EDITING, headersToColumnInfo, "story_id,editor_id", references));
		
		// story_to_genre
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("genre_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("genre_id", GENRE, "id"));
		
		createTable(createTableQuery(STORY_TO_GENRE, headersToColumnInfo, "story_id,genre_id", references));
		
		// story_to_characters
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("story_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("character_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("story_id", "story", "id"));
		references.add(new Reference("character_id", CHARACTERS, "id"));
		
		createTable(createTableQuery(STORY_TO_CHARACTERS, headersToColumnInfo, "story_id,character_id", references));
		
		// issue_in_series
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("issue_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("series_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("issue_id", "issue", "id"));
		references.add(new Reference("series_id", "series", "id"));
		
		createTable(createTableQuery(ISSUE_IN_SERIES, headersToColumnInfo, "issue_id,series_id", references));
		
		// issue_to_editing
		headersToColumnInfo = new HashMap<>();
		references = new ArrayList<>();
		
		headersToColumnInfo.put("issue_id", new ColumnInfo(Type.INT, null, false));
		headersToColumnInfo.put("editor_id", new ColumnInfo(Type.INT, null, false));
		
		references.add(new Reference("issue_id", "issue", "id"));
		references.add(new Reference("editor_id", EDITOR, "id"));
		
		createTable(createTableQuery(ISSUE_TO_EDITING, headersToColumnInfo, "issue_id,editor_id", references));
		
	}

	private void parseAndCreateNewRelations(String name, Map<String, NewRelationInfo> headersToNewRelations,
			Map<String, ColumnInfo> headersToColumnInfo)
			throws IOException {

		// Write headers for new relations
		for (NewRelationInfo newRelationInfo : headersToNewRelations.values()) {
			newRelationInfo.writer.write(newRelationInfo.newRelationHeader + '\n');
		}

		// Create main writer (the one writing the same file than the one read,
		// but in parsed directory)
		BufferedWriter mainWriter = newWriter(name.split("-")[0]);

		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(this.pathToParse + name + this.fileType)))) {
			boolean isHeader = true;
			List<String> headers = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] allData = Utils.splitAndTrim(line.replace("\"", "").replace("'", ""), ",");
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
					assert(allData.length == headers.size());
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
							//System.out.println(header);
							if (newRelationInfo == null) {
								data = sanitizeData(data, headersToColumnInfo.get(header));
								// Normal column
								mainWriter.write(delimiter + data);
								delimiter = ",";
								if (header.equals("id")) {
									id = Integer.parseInt(data);
								}
							} else {
								// Column that will collapse and create a new
								// Relation
								newRelationInfo.addNewElements(data, id, headersToColumnInfo.get(header));
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

	private String sanitizeData(String data, ColumnInfo colInfo) {
		switch (colInfo.type) {
		case INT:
			try {
				return Integer.parseInt(data)+"";
			}
			catch (NumberFormatException e) {
				return NULL;
			}
		case REAL:
			try {
				return Double.parseDouble(data)+"";
			}
			catch (NumberFormatException e) {
				return NULL;
			}
		case VARCHAR:
			if (data.length() > colInfo.maxCharacters) {
				return NULL;
			}
			break;
		default:
			break;
		
		}
		return data;
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
	
	private void createEntityTable(String name, Integer maxCharForName) throws SQLException {
		Map<String, ColumnInfo> headersToColumnInfo = new HashMap<>();
		headersToColumnInfo.put("id", new ColumnInfo(Type.INT, null, true));
		headersToColumnInfo.put("name", new ColumnInfo(Type.VARCHAR, maxCharForName, true));
		
		createTable(createTableQuery(name, headersToColumnInfo, "id", new ArrayList<>()));
	}
	
	private void createTable(String query) throws SQLException {
		System.out.println(query);
		System.out.println();
		
		/*
		if (query.trim().length() > 0) {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		}
		*/
	}
	
	private String createTableQuery(String tableName, Map<String, ColumnInfo> headersToColumnInfo, String primary, List<Reference> references) {

		System.out.println("##### Creating table: " + tableName);
		
		String query = "CREATE TABLE " + tableName + "(";

		String comma = "";
		
		for (Map.Entry<String, ColumnInfo> entry : headersToColumnInfo.entrySet()) {
			ColumnInfo colInfo = entry.getValue();
			
			query += comma;
			comma = ", ";
			
			query += entry.getKey() + " ";
			
			query += colInfo.type.toSQLString();
			
			if (colInfo.type.equals(Type.VARCHAR)) {
				query += "(" + colInfo.maxCharacters + ")";
			}
			if (colInfo.type.equals(Type.REAL)) {
				query += "(*," + DEFAULT_DECIMAL_SCALE + ")";
			}
			
			if (!colInfo.nullable) {
				query += " NOT NULL";
			}
		}
		
		if (!primary.equals("")) {
			query += ", PRIMARY KEY (" + primary + ")";
		}

		for (Reference ref : references) {
			query += ", FOREIGN KEY (" + ref.refKey + ") REFERENCES " + ref.otherTable + "(" + ref.keyInOtherTable + ")";
		}

		query += ")";

		return query;
	}
	
	static class Reference {
		
		public String refKey;
		public String otherTable;
		public String keyInOtherTable;
		
		public Reference(String refKey, String otherTable, String keyInOtherTable) {
			this.refKey = refKey;
			this.otherTable = otherTable;
			this.keyInOtherTable = keyInOtherTable;
		}
		
	}
	
	// Store information about a column (type, constraint, ...)
	static class ColumnInfo {
		
		public Type type;
		public Integer maxCharacters; // NULL if not VARCHAR
		public boolean nullable;
		
		public ColumnInfo(Type type, Integer maxCharacters, boolean nullable) {
			this.type = type;
			this.maxCharacters = maxCharacters;
			this.nullable = nullable;
		}
		
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
		public void addNewElements(String data, int id, ColumnInfo colInfo) throws IOException {
			String[] elements = Utils.splitAndTrim(data, ";");
			for (String elem : elements) {
				assert(colInfo.type.equals(Type.VARCHAR));
				if (elem.length() > 0 && (colInfo == null || elem.length() <= colInfo.maxCharacters)) {
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
