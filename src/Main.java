import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

public class Main {

	public static final int EXPECTED_NB_FILES_TO_PARSE = 12;

	private static final String PATH_TO_PARSE = "comicsInitData/";
	private static final String PARSED_PATH = "comicsParsedData/";
	private static final String FILE_TYPE = ".csv";

	private static final String PWD_FILE = "src/pwd.txt";

	private static Connection conn;


    public static String runQuery(String query) throws SQLException {
        //query the sql database with the string argument
        Statement stmt;

        stmt = conn.createStatement();

        String answer = String.valueOf(stmt.executeQuery(query));

        return answer;

    }

    /**
     * retrieve the queries, which are all stored on the line corresponding to their number. Now simply access
     * the one you want with the corresponding index
     *
     * @return queries the list of all the predefined queries
     */
    private static List<String> parsePredefinedQueries() throws IOException {
        //retrieve the queries, which are all stored on the line corresponding to their number. Now simply access
        //the one you want wit
        //returns NULL if not all the queries have been
        List<String> queries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File("predefined_queries.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!queries.add(line)) {
                    throw new IOException("The line in predefined_queries.txt could not be read.");
                }
            }
        }

        return queries;
    }

    /**
     * Uses the runQuery() and parsePredefinedQueries() to compute the list of answers of all the
     * predefined queries
     *
     * @return a list containing all the answers to all the predefined queries
     */
    public static List<String> allPredefinedQueriesAnswers() {

        List<String> answers = new ArrayList<>();

        try {
            List<String> queries = parsePredefinedQueries();

            for (String query : queries) {
                answers.add(runQuery(query));
            }
        } catch (Exception e) {
            System.err.println("Error while creating the set of done table");
            e.printStackTrace();
        }

        return answers;
    }
	
	public static void main(String[] args) throws IOException, SQLException {
        // Connection management
        // DB2017_G21@//diassrv2.epfl.ch:1521/orcldias
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            String s1 = "jdbc:oracle:thin:@//diassrv2.epfl.ch:1521/orcldias.epfl.ch";
            String s2;
            try (BufferedReader buffer = new BufferedReader(new FileReader(PWD_FILE))) {
            	s2 = buffer.readLine().trim();
            }

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
        System.out.println("----- Start parsing -----");
		new Parser(PATH_TO_PARSE, PARSED_PATH, FILE_TYPE, conn).parse();
		
		/*
        String[] names = {"story_to_feature", "story_to_script", "story_to_pencils", "story_to_inks", "story_to_colors",
        		"story_to_letters", "story_to_editing", "story_to_genre", "story_to_characters", "issue_to_editing", "issue_in_series"};
        for (String name : names) {
        	Utils.rewriteRelationFile(new File(PARSED_PATH + name + FILE_TYPE));
        }
        */

		System.out.println("----- End parsing -----");

        //Closing connection management
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error while closing connection ");
            e.printStackTrace();
        }
	}
}