import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

public class InsertCSV {

	private final String TABLE_DONE_FILE = "TableDone.txt";

	private final HashSet<String> tableDone;

	private final String PARSED_PATH;
	private final String INPUT_FILE_TYPE = ".csv";

	/*
	 * private final String DB_NAME = "IntroDBProject";
	 * 
	 * // Database credentials private final String USER = "root"; private final
	 * String PASS = "1234";
	 * 
	 * 
	 * // JDBC driver name and database URL private final String JDBC_DRIVER =
	 * "com.mysql.jdbc.Driver"; private final String DB_URL =
	 * "jdbc:mysql://localhost/" + DB_NAME;
	 */

	private final String DB_NAME = "DB2017_G21";

	private final int amountDataInOneTime = 10000;

	private Connection conn;

	public InsertCSV(String parsedPath) {

		PARSED_PATH = parsedPath;

		try {
			/*
			 * // STEP 2: Register JDBC driver Class.forName(JDBC_DRIVER);
			 * 
			 * // STEP 3: Open a connection
			 * System.out.println("Connecting to database..."); conn =
			 * DriverManager.getConnection(DB_URL, USER, PASS);
			 */
			Class.forName("oracle.jdbc.driver.OracleDriver");

			String s1 = "jdbc:oracle:thin:@//diassrv2.epfl.ch:1521/orcldias.epfl.ch";
			String s2 = "DB2017_G21";

			Properties p = new Properties();
			p.setProperty("user", s2);
			p.setProperty("password", s2);
			conn = DriverManager.getConnection(s1, p);

		} catch (SQLException se) {
			System.err.println("Handle errors for JDBC");
			se.printStackTrace();
			System.exit(40);
		} catch (Exception e) {
			System.err.println("Handle errors for Class.forName");
			e.printStackTrace();
			System.exit(41);
		}

		tableDone = new HashSet<>();
		try {
			File tdf = new File(TABLE_DONE_FILE);
			if (!tdf.exists()) {
				tdf.createNewFile();
			}

			BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream(TABLE_DONE_FILE)));
			while (stream.ready()) {
				tableDone.add(stream.readLine());
			}
			stream.close();

		} catch (Exception e) {
			System.err.println("Error while creating the set of done table");
			e.printStackTrace();
		}

		insertCSV();

		try {
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error while closing connection ");
			e.printStackTrace();
		}
	}

	private void launchDBWork(String tableName, String[][] column, String primary, String[][] references,
			PrintWriter doneTableFile) {
		try {
			if (!tableDone.contains(tableName)) {
				insertTableAndData(tableName, column, primary, references);
				doneTableFile.println(tableName);
				doneTableFile.flush();
			}
		} catch (IOException | SQLException e) {
			System.err.println("Error while inserting : " + tableName);
			e.printStackTrace();

			doneTableFile.close();
			System.exit(42);
		}
	}

	public void insertCSV() {

		final String INT = "NUMBER";
		final String VARCHAR = "VARCHAR";
		final String DATE = "DATE";
		final String REAL = "NUMBER";

		final String TRUE = "T";
		final String FALSE = "F";
		final String N_100 = "200";
		final String N_1000 = "1000";

		PrintWriter doneTableFile = null;
		try {
			doneTableFile = new PrintWriter(new BufferedWriter(new FileWriter(TABLE_DONE_FILE, true)));
		} catch (IOException e1) {
			System.err.println("Error while opening the done table file to write new table");
			e1.printStackTrace();
			return;
		}

		/* story_type */
		final String storyType = "story_type";
		final String[][] argsStoryType = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE } };
		final String primaryStoryType = "id";
		final String[][] refStoryType = null;

		launchDBWork(storyType, argsStoryType, primaryStoryType, refStoryType, doneTableFile);

		/* language */
		final String language = "language";
		final String[][] argsLan = { { "id", INT, null, FALSE }, { "code", VARCHAR, N_100, FALSE },
				{ "name", VARCHAR, N_100, FALSE } };
		final String primaryLan = "id";
		final String[][] refLan = null;

		launchDBWork(language, argsLan, primaryLan, refLan, doneTableFile);

		/* country */
		final String country = "country";
		final String[][] argsCou = { { "id", INT, null, FALSE }, { "code", VARCHAR, N_100, FALSE },
				{ "name", VARCHAR, N_100, FALSE } };
		final String primaryCou = "id";
		final String[][] refCou = null;

		launchDBWork(country, argsCou, primaryCou, refCou, doneTableFile);

		/* series_publication_type */
		final String spt = "series_publication_type";
		final String[][] argsSPT = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE } };
		final String primarySPT = "id";
		final String[][] refSPT = null;

		launchDBWork(spt, argsSPT, primarySPT, refSPT, doneTableFile);

		/* artist */
		final String artist = "artist";
		final String[][] argsArt = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE } };
		final String primaryArt = "id";
		final String[][] refArt = null;

		launchDBWork(artist, argsArt, primaryArt, refArt, doneTableFile);

		/* characters */
		final String chara = "characters";
		final String[][] argsChara = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE } };
		final String primaryChara = "id";
		final String[][] refChara = null;

		launchDBWork(chara, argsChara, primaryChara, refChara, doneTableFile);

		/* editor */
		final String editor = "editor";
		final String[][] argsEdi = { { "id", INT, null, FALSE }, { "name", VARCHAR, "200", FALSE } };
		final String primaryEdi = "id";
		final String[][] refEdi = null;

		launchDBWork(editor, argsEdi, primaryEdi, refEdi, doneTableFile);

		/* genre */
		final String genre = "genre";
		final String[][] argsGen = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE } };
		final String primaryGen = "id";
		final String[][] refGen = null;

		launchDBWork(genre, argsGen, primaryGen, refGen, doneTableFile);

		/* publisher */
		final String publisher = "publisher";
		final String[][] argsPub = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE },
				{ "country_id", INT, null, TRUE }, { "year_began", DATE, null, FALSE },
				{ "year_ended", DATE, null, FALSE }, { "notes", VARCHAR, N_1000, FALSE },
				{ "url", VARCHAR, N_1000, FALSE } };
		final String primaryPub = "id";
		final String[][] refPub = { { "country_id", "country", "id" } };

		launchDBWork(publisher, argsPub, primaryPub, refPub, doneTableFile);

		/* brand_group */
		final String bg = "brand_group";
		final String[][] argsBG = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE },
				{ "year_began", DATE, null, FALSE }, { "year_ended", DATE, null, FALSE },
				{ "notes", VARCHAR, N_1000, FALSE }, { "url", VARCHAR, N_1000, FALSE },
				{ "publisher_id", INT, null, TRUE } };
		final String primaryBG = "id";
		final String[][] refBG = { { "publisher_id", "publisher", "id" } };

		launchDBWork(bg, argsBG, primaryBG, refBG, doneTableFile);

		/* indicia_publisher */
		final String IP = "indicia_publisher";
		final String[][] argsIP = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE },
				{ "publisher_id", INT, null, TRUE }, { "country_id", INT, null, TRUE },
				{ "year_began", DATE, null, FALSE }, { "year_ended", DATE, null, FALSE },
				{ "is_surrogate", VARCHAR, "1", FALSE }, { "notes", VARCHAR, N_1000, FALSE },
				{ "url", VARCHAR, N_1000, FALSE }, };
		final String primaryIP = "id";
		final String[][] refIP = { { "publisher_id", "publisher", "id" }, { "country_id", "country", "id" } };

		launchDBWork(IP, argsIP, primaryIP, refIP, doneTableFile);

		/* issue */
		final String issue = "issue";
		final String[][] argsIss = { { "id", INT, null, FALSE }, { "issue_number", INT, null, FALSE },
				{ "indicia_publisher_id", INT, null, /*TRUE*/ FALSE }, { "publication_date", DATE, null, FALSE },
				{ "price", REAL, null, FALSE }, { "page_count", INT, null, FALSE },
				{ "indicia_frequency", INT, null, FALSE }, { "notes", VARCHAR, N_100, FALSE },
				{ "isbn", INT, null, FALSE }, { "valid_isbn", INT, null, FALSE }, { "barcode", INT, null, FALSE },
				{ "title", VARCHAR, N_100, FALSE }, { "on_sale_date", DATE, null, FALSE },
				{ "rating", REAL, null, FALSE } };
		final String primaryIss = "id";
		final String[][] refIss = { { "indicia_publisher_id", "indicia_publisher", "id" } };

		launchDBWork(issue, argsIss, primaryIss, refIss, doneTableFile);

		/* series */
		final String series = "series";
		final String[][] argsSer = { { "id", INT, null, FALSE }, { "name", VARCHAR, N_100, FALSE },
				{ "format", VARCHAR, N_100, FALSE }, { "year_began", DATE, null, FALSE },
				{ "year_ended", DATE, null, FALSE }, { "publication_dates", VARCHAR, N_1000, FALSE },
				{ "first_issue_id", INT, null, TRUE }, { "last_issue_id", INT, null, FALSE },
				{ "publisher_id", INT, null, TRUE }, { "country_id", INT, null, TRUE },
				{ "language_id", INT, null, TRUE }, { "notes", VARCHAR, N_100, FALSE },
				{ "color", VARCHAR, N_100, FALSE }, { "dimensions", VARCHAR, N_100, FALSE },
				{ "paper_stock", VARCHAR, N_100, FALSE }, { "bidning", VARCHAR, N_100, FALSE },
				{ "publishing_format", VARCHAR, N_100, FALSE }, { "publication_type_id", INT, null, TRUE } };
		final String primarySer = "id";
		final String[][] refSer = { { "first_issue_id", "issue", "id" }, { "last_issue_id", "issue", "id" },
				{ "publisher_id", "publisher", "id" }, { "country_id", "country", "id" },
				{ "language_id", "language", "id" }, { "publication_type_id", "publication_type", "id" },

		};

		launchDBWork(series, argsSer, primarySer, refSer, doneTableFile);

		/* issue_reprint */
		final String IR = "issue_reprint";
		final String[][] argsIR = { { "id", INT, null, FALSE }, { "origin_issue_id", INT, null, TRUE },
				{ "target_issue_id", INT, null, TRUE } };
		final String primaryIR = "id";
		final String[][] refIR = { { "origin_issue_id", "issue", "id" }, { "target_issue_id", "issue", "id" } };

		launchDBWork(IR, argsIR, primaryIR, refIR, doneTableFile);

		/* issue_in_series */
		final String issAndSer = "issue_in_series";
		final String[][] argsIS = { { "issue_id", INT, null, TRUE }, { "series_id", INT, null, TRUE } };
		final String primaryIS = "";
		final String[][] refIS = { { "issue_id", "issue", "id" }, { "series_id", "serie", "id" } };

		launchDBWork(issAndSer, argsIS, primaryIS, refIS, doneTableFile);

		/* issue_to_editing */
		final String issAndEd = "issue_to_editing";
		final String[][] argsIE = { { "issue_id", INT, null, TRUE }, { "editor_id", INT, null, TRUE } };
		final String primaryIE = "";
		final String[][] refIE = { { "issue_id", "issue", "id" }, { "editor_id", "editor", "id" } };

		launchDBWork(issAndEd, argsIE, primaryIE, refIE, doneTableFile);

		/* story */
		final String story = "story";
		final String[][] argsStory = { { "id", INT, null, FALSE }, { "title", VARCHAR, N_100, FALSE },
				{ "issue_id", INT, null, FALSE }, { "synopsis", VARCHAR, N_1000, FALSE },
				{ "reprint_notes", VARCHAR, N_100, FALSE }, { "notes", VARCHAR, N_100, FALSE },
				{ "type_id", INT, null, TRUE } };
		final String primaryStory = "id";
		final String[][] refStory = { { "issue_id", "issue", "id" }, { "type_id", "story_type", "id" } };

		launchDBWork(story, argsStory, primaryStory, refStory, doneTableFile);

		/* sotry_reprint */
		final String storyRe = "story_reprint";
		final String[][] argsSR = { { "id", INT, null, FALSE }, { "origin_id", INT, null, TRUE },
				{ "target_id", INT, null, TRUE } };
		final String primarySR = "id";
		final String[][] refSR = { { "origin_id", "story", "id" }, { "target_id", "story", "id" } };

		launchDBWork(storyRe, argsSR, primarySR, refSR, doneTableFile);

		/* story_to_characters */
		final String SToChara = "story_to_characters";
		final String[][] argsSChara = { { "story_id", INT, null, TRUE }, { "character_id", INT, null, TRUE } };
		final String primarySChara = "";
		final String[][] refSChara = { { "story_id", "story", "id" }, { "character_id", "characters", "id" } };

		launchDBWork(SToChara, argsSChara, primarySChara, refSChara, doneTableFile);

		/* story_to_colors */
		final String SToColor = "story_to_colors";
		final String[][] argsSColor = { { "story_id", INT, null, TRUE }, { "artist_id", INT, null, TRUE } };
		final String primarySColor = "";
		final String[][] refSColor = { { "story_id", "story", "id" }, { "artist_id", "artist", "id" } };

		launchDBWork(SToColor, argsSColor, primarySColor, refSColor, doneTableFile);

		/* story_to_feature */
		final String SToFeature = "story_to_feature";
		final String[][] argsSFeature = { { "story_id", INT, null, TRUE }, { "artist_id", INT, null, TRUE } };
		final String primarySFeature = "";
		final String[][] refSFeature = { { "story_id", "story", "id" }, { "artist_id", "artist", "id" } };

		launchDBWork(SToFeature, argsSFeature, primarySFeature, refSFeature, doneTableFile);

		/* story_to_editing */
		final String SToEditor = "story_to_editing";
		final String[][] argsSEditor = { { "story_id", INT, null, TRUE }, { "editor_id", INT, null, TRUE } };
		final String primarySEditor = "";
		final String[][] refSEditor = { { "story_id", "story", "id" }, { "editor_id", "editor", "id" } };

		launchDBWork(SToEditor, argsSEditor, primarySEditor, refSEditor, doneTableFile);

		/* story_to_genre */
		final String SToGenre = "story_to_genre";
		final String[][] argsSGenre = { { "story_id", INT, null, TRUE }, { "genre_id", INT, null, TRUE } };
		final String primarySGenre = "";
		final String[][] refSGenre = { { "story_id", "story", "id" }, { "genre_id", "genre", "id" } };

		launchDBWork(SToGenre, argsSGenre, primarySGenre, refSGenre, doneTableFile);

		/* story_to_inks */
		final String SToInk = "story_to_inks";
		final String[][] argsSInk = { { "story_id", INT, null, TRUE }, { "artist_id", INT, null, TRUE } };
		final String primarySInk = "";
		final String[][] refSInk = { { "story_id", "story", "id" }, { "artist_id", "artist", "id" } };

		launchDBWork(SToInk, argsSInk, primarySInk, refSInk, doneTableFile);

		/* story_to_letters */
		final String SToLetter = "story_to_letters";
		final String[][] argsSLetter = { { "story_id", INT, null, TRUE }, { "artist_id", INT, null, TRUE } };
		final String primarySLetter = "";
		final String[][] refSLetter = { { "story_id", "story", "id" }, { "artist_id", "artist", "id" } };

		launchDBWork(SToLetter, argsSLetter, primarySLetter, refSLetter, doneTableFile);

		/* story_to_pencils */
		final String SToPen = "story_to_pencils";
		final String[][] argsSPen = { { "story_id", INT, null, TRUE }, { "artist_id", INT, null, TRUE } };
		final String primarySPen = "";
		final String[][] refSPen = { { "story_id", "story", "id" }, { "artist_id", "artist", "id" } };

		launchDBWork(SToPen, argsSPen, primarySPen, refSPen, doneTableFile);

		/* story_to_script */
		final String SToScr = "story_to_scripts";
		final String[][] argsSScr = { { "story_id", INT, null, TRUE }, { "artist_id", INT, null, TRUE } };
		final String primarySScr = "";
		final String[][] refSScr = { { "story_id", "story", "id" }, { "artist_id", "artist", "id" } };

		launchDBWork(SToScr, argsSScr, primarySScr, refSScr, doneTableFile);
	}

	private boolean tableExist(String tableName) {

		boolean exist = false;

		try {
			DatabaseMetaData md = conn.getMetaData();

			ResultSet rs = md.getTables(null, null, tableName.toUpperCase(), null);

			while (rs.next()) {
				exist = true;
			}

		} catch (SQLException e) {
			System.err.println("Can't access metaData to verify existenz");
			e.printStackTrace();
		}

		return exist;
	}

	private void createTable(String query) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
	}

	private void deleteItemsFromTableAndTable(String tableName) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("delete from " + tableName + "");
		stmt.executeUpdate("drop table " + tableName + "");
		stmt.close();
	}

	private String createTableQuery(String tableName, String[][] column, String primary, String[][] references) {

		String query = "CREATE TABLE " + tableName + "(";

		String comma = "";
		for (int i = 0; i < column.length; i++) {

			query += comma;
			comma = ", ";

			query += column[i][0] + " ";

			query += column[i][1];

			if (column[i][1].equals("VARCHAR")) {
				query += "(" + column[i][2] + ")";
			}

			if (column[i][3].equals("T")) {
				query += " NOT NULL";
			}
		}

		if (!primary.equals("")) {
			query += ", PRIMARY KEY (" + primary + ")";
		}

		for (int i = 0; references != null && i < references.length; i++) {

			query += ", FOREIGN KEY (" + references[i][0] + ") REFERENCES " + references[i][1] + "(" + references[i][2]
					+ ")";

		}

		query += ")";

		return query;
	}

	private void appendValuesSpaceSQL(StringBuilder sb, int nRow, int nCol) {

		for (int i = 0; i < nRow; i++) {

			if (i != 0) {
				sb.append(",");
			}

			sb.append("(");
			for (int j = 0; j < nCol; j++) {
				if (j != 0) {
					sb.append(",");
				}
				sb.append("?");
			}

			sb.append(")");
		}
	}

	private void appendValuesSpaceOracle(StringBuilder sb, String tableName, int nRow, int nCol) {

		for (int i = 0; i < nRow; i++) {

			sb.append("INTO ").append(tableName).append(" VALUES ");

			sb.append("(");
			for (int j = 0; j < nCol; j++) {
				if (j != 0) {
					sb.append(",");
				}
				sb.append("?");
			}

			sb.append(")").append("\n");
		}
	}

	private void insertTableAndData(String tableName, String[][] column, String primary, String[][] references)
			throws IOException, SQLException {

		System.out.println("Start insertion in " + tableName);

		System.out.println(createTableQuery(tableName, column, primary, references));

		if (!tableExist(tableName)) {
			createTable(createTableQuery(tableName, column, primary, references));
		} else {
			deleteItemsFromTableAndTable(tableName);
			createTable(createTableQuery(tableName, column, primary, references));
		}

		System.out.println("-----> Table Creation Done");

		BufferedReader stream = new BufferedReader(
				new InputStreamReader(new FileInputStream(PARSED_PATH + tableName + INPUT_FILE_TYPE)));

		boolean running = true;
		int lineCount = 0;
		stream.readLine();
		while (running) {

			ArrayList<String> tmp = new ArrayList<>();
			while (stream.ready() && tmp.size() < amountDataInOneTime) {
				String li = stream.readLine();
				if (!li.equals("NULL") && !li.equals(""))
					tmp.add(li);
			}

			StringBuilder sqlQuery = new StringBuilder();
			/*
			 * sqlQuery.append("INSERT INTO ").append(tableName).
			 * append(" VALUES "); appendValuesSpaceSQL(sqlQuery, tmp.size(),
			 * column.length); sqlQuery.append(";");
			 */

			sqlQuery.append("INSERT ALL").append("\n");
			appendValuesSpaceOracle(sqlQuery, tableName, tmp.size(), column.length);
			sqlQuery.append("SELECT * FROM dual");

			// System.out.println(sqlQuery.toString());

			PreparedStatement ps = conn.prepareStatement(sqlQuery.toString());

			int c = 0;
			for (String line : tmp) {

				String[] data = line.split(",");

				for (int i = 0; i < data.length; i++) {

					switch (column[i][1]) {
					case "NUMBER":
						if (data[i].equals("NULL") || data[i].contains("none") || data[i].contains("None")
								|| data[i].contains("[") || data[i].contains("nn")) {
							ps.setNull(++c, Types.INTEGER);
						} else if (data[i].contains("FREE")) {
							ps.setFloat(++c, (float) 0.0);
						} else {
							try {
								c = c + 1;
								ps.setInt(c, Integer.parseInt(data[i]));
							} catch (NumberFormatException e1) {

								try {
									ps.setFloat(c, Float.parseFloat(data[i]));
								} catch (Exception e2) {
									ps.setNull(c, Types.INTEGER);
								}
							}
						}
						break;
					case "VARCHAR":
						if (data[i].equals("NULL") || data[i].length() > Integer.parseInt(column[i][2])) {
							ps.setNull(++c, Types.VARCHAR);
						} else {
							ps.setString(++c, data[i]);
						}
						break;
					/*
					 * case "NUMBER": if (data[i].equals("NULL")) {
					 * ps.setNull(++c, Types.FLOAT); } else { ps.setFloat(++c,
					 * Float.parseFloat(data[i])); } break;
					 */
					case "DATE":
						/*
						 * if (data[i].equals("NULL") || data[i].contains("["))
						 * { ps.setNull(++c, Types.DATE); break; } else {
						 */
						String[] tmpDateData = data[i].split(" ");
						Date d = null;
						/*
						 * if (tmpDateData.length == 1 && !data[i].contains("-")
						 * && Integer.parseInt(tmpDateData[0]) > 2030) {
						 * ps.setNull(++c, Types.DATE); break; } else
						 * if(tmpDateData.length == 1 && data[i].contains("-"))
						 * { ps.setDate(++c, Date.valueOf(data[i])); break; }
						 */
						try {
							if (tmpDateData.length == 1) {
								d = new Date(Date.parse("1 January " + tmpDateData[0]));
							} else if (tmpDateData.length == 2) {
								d = new Date(Date.parse("1 " + tmpDateData[0] + " " + tmpDateData[1]));
							} else if (tmpDateData.length == 3) {
								d = new Date(Date.parse(tmpDateData[0] + " " + tmpDateData[1] + " " + tmpDateData[2]));
							} else {
								System.err.println("Unkown date format : " + data[i]);
							}
							ps.setDate(++c, d);
						} catch (Exception e) {
							ps.setNull(++c, Types.DATE);
						}
						break;
					default:
						System.err.println("Unknown type" + column[i][1]);
					}
				}
			}

			System.out.println("--> launch on oracle ...");

			long start = System.currentTimeMillis();
			
			ps.executeUpdate();
			ps.close();

			System.out.println("----> done on oracle : " + ((System.currentTimeMillis() - start) / 1000) + " ms");

			lineCount += tmp.size();
			System.out.println("** Tot. inserted : " + lineCount);

			if (tmp.size() != amountDataInOneTime) {
				running = false;
			}
		}

		stream.close();

		System.out.println("--- Done inserting " + tableName);
	}
}
