
//STEP 1. Import required packages
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class Main {

	private static final String PATH_TO_PARSE = "comicsInitData/";
	private static final String PARSED_PATH = "comicsParsedData/";
	private static final String FILE_TYPE = ".csv";
	
	public static void main(String[] args) throws IOException {

		System.out.println("----- Start parsing -----");
		new ParseCSV(PATH_TO_PARSE, PARSED_PATH, FILE_TYPE).parseCSV();

		/*
		System.out.println("----- Start inserting -----");
		new InsertCSV(PARSED_PATH).insertCSV();
		*/

		System.out.println("Done");
	}
}