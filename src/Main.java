import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Main {

	public static final int EXPECTED_NB_FILES_TO_PARSE = 12;
	
	private static final String PATH_TO_PARSE = "comicsInitData/";
	private static final String PARSED_PATH = "comicsParsedData/";
	private static final String FILE_TYPE = ".csv";

	private static Connection conn;

    public static String runQuery(String query) throws SQLException {
        //query the sql database with the string argument
        Statement stmt;

        stmt = conn.createStatement();

        String answer = String.valueOf(stmt.executeQuery(query));

        return answer;

    }
    

    public static String retrievePredefinedQuery(int number) {
        //retrieve the query, which are all stored on the line corresponding to their number

        //return it
        return "";
    }
	
	public static void main(String[] args) throws IOException {

		System.out.println("----- Start parsing -----");
		//new Parser(PATH_TO_PARSE, PARSED_PATH, FILE_TYPE).parse();

        // Connection management
        // DB2017_G21@//diassrv2.epfl.ch:1521/orcldias
        try {
			/*
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
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

		
		System.out.println("----- Start inserting -----");
		new InsertCSV(PARSED_PATH, conn);

        //Closing connection management
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error while closing connection ");
            e.printStackTrace();
        }
		
/*
		System.out.println("Done");
		System.out.println(new Date(Date.parse(" 1 January 1998")).toString());
*/	}
	
	
}