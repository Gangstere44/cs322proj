
//STEP 1. Import required packages
import java.sql.*;

public class Main {

	public static void main(String[] args) {

		final String PATH_TO_PARSE = "comicsInitData/";
		final String PARSED_PATH = "comicsParsedData/";
		
		System.out.println("----- Start parsing -----");
		new ParseCSV(PATH_TO_PARSE, PARSED_PATH).parseCSV();

		System.out.println("----- Start inserting -----");
		new InsertCSV(PARSED_PATH).insertCSV();

		System.out.println("Done");
	}
}