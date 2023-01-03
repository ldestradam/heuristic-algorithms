package mx.com.lestradam.algorithms.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVWriter;

import mx.com.lestradam.algorithms.exceptions.DataException;

public class CsvWriter {
	
	private static final String[] HEADER_EDGES = {"Source", "Target", "Weight", "Type"};
	
	private CsvWriter() {}
	
	public static void createEdgeFile(final String filePath, final List<String[]> rows) {
		try (CSVWriter file = new CSVWriter(new FileWriter(filePath, true))){
			file.writeNext(HEADER_EDGES);
			for (String[] row : rows) {
				file.writeNext(row);
			}
		} catch (IOException e) {
			throw new DataException("Error writing node file", e);
		}
	}

}
