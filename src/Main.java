import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

	public static final int EXPECTED_NB_FILES_TO_PARSE = 12;

	private static final String PATH_TO_PARSE = "comicsInitData/";
	private static final String PARSED_PATH = "comicsParsedData/";
	private static final String FILE_TYPE = ".csv";

	public static void main(String[] args) throws IOException {

		/*
		 * System.out.println("----- Start parsing -----"); //new
		 * Parser(PATH_TO_PARSE, PARSED_PATH, FILE_TYPE).parse();
		 * 
		 * 
		 * System.out.println("----- Start inserting -----"); new
		 * InsertCSV(PARSED_PATH);
		 * 
		 * System.out.println("Done");
		 */

		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			try {
				DriverManager.getConnection("jdbc:mysql://itriputrrxjeepfl.mysql.db:3306/itriputrrxjeepfl", "itriputrrxjeepfl", "Zomba2932431");				
			} catch (SQLException e) {
				System.out.println("crotte2");
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			System.out.println("crotte");
			e.printStackTrace();
		}

	}

}