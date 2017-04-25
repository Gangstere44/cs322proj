
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class ParseCSV {

	private final String INPUT_FILE_TYPE = ".csv";

	private final String PATH_TO_PARSE;
	private final String PARSED_PATH;

	public ParseCSV(String pathToParse, String parsedPath) {
		
		PATH_TO_PARSE = pathToParse;
		PARSED_PATH = parsedPath;
		
	};

	public void parseCSV() {
		
	}


}
