import java.io.IOException;

public class Main {

	public static final int EXPECTED_NB_FILES_TO_PARSE = 12;
	
	private static final String PATH_TO_PARSE = "comicsInitData/";
	private static final String PARSED_PATH = "comicsParsedData/";
	private static final String FILE_TYPE = ".csv";
	
	public static void main(String[] args) throws IOException {

		System.out.println("----- Start parsing -----");
		new Parser(PATH_TO_PARSE, PARSED_PATH, FILE_TYPE).parse();

		/*
		System.out.println("----- Start inserting -----");
		new InsertCSV(PARSED_PATH).insertCSV();
		*/

		System.out.println("Done");
	}
}