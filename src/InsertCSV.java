import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class InsertCSV {

	private final String PARSED_PATH;
	private final String INPUT_FILE_TYPE = ".csv";
	
	private final String DB_NAME = "IntroDBProject";
	
	// Database credentials
	private final String USER = "root";
	private final String PASS = "1234";
	
	// JDBC driver name and database URL
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;
	
	private Connection conn;
	
	public InsertCSV(String parsedPath) {
		
		PARSED_PATH = parsedPath;
		
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	
		
	}
	
	public void insertCSV() {
		
	}
	
	private boolean tableExist(String tableName) {

		boolean exist = false;

		try {
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, tableName, null);
			while (rs.next()) {
				exist = true;
			}

		} catch (SQLException e) {
			System.err.println("Can't access metaData to verify existenz");
			e.printStackTrace();
		}

		return exist;
	}

	private void createTable(String tableName, String args) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("create table " + tableName + " " + args + ");");
		stmt.close();
	}

	private void deleteItemsFromTable(String tableName) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("delete from " + tableName + ";");
		stmt.close();
	}
	
	private void InsertStoryType() throws SQLException, IOException {

		final String TABLE_NAME = "story_type";
		final String ARGS = "(id int, name varchar(100), primary key (id)";

		if (!tableExist(TABLE_NAME)) {
			createTable(TABLE_NAME, ARGS);
		} else {
			deleteItemsFromTable(TABLE_NAME);
		}

		Statement stmt = conn.createStatement();
		StringBuilder sqlQuery = new StringBuilder();

		BufferedReader stream = new BufferedReader(
				new InputStreamReader(new FileInputStream(PARSED_PATH + TABLE_NAME + INPUT_FILE_TYPE)));

		System.out.println("Start inserting in " + TABLE_NAME);

		ResultSet rs = stmt.executeQuery("select * from story_type where 1 = 2");
		ResultSetMetaData res = rs.getMetaData();
		// first line always the name of the args
		String line = stream.readLine();
		line = stream.readLine();
		while (line != null) {

			// remove useless comment
			line = line.replaceAll("\\*.*?\\*", "");

			String[] data = line.split(",");
			sqlQuery = new StringBuilder();
			sqlQuery.append("insert into ").append(TABLE_NAME).append(" values (");

			String comma = "";
			for (int i = 0; i < data.length; i++) {

				sqlQuery.append(comma);
				comma = ",";

				if (data[i].equals("")) {
					sqlQuery.append("NULL");
				} else {
					if (res.getColumnType(i + 1) == Types.VARCHAR) {
						sqlQuery.append("'").append(data[i]).append("'");
					} else {
						sqlQuery.append(data[i]);
					}
				}
			}

			sqlQuery.append(");");
			System.out.println(sqlQuery.toString());

			stmt.executeUpdate(sqlQuery.toString());

			line = stream.readLine();
		}

		System.out.println("Done inserting");

		stmt.close();
		stream.close();
	}
}
