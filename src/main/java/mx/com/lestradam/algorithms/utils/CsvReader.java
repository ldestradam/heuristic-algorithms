package mx.com.lestradam.algorithms.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.data.Node;
import mx.com.lestradam.algorithms.exceptions.DataException;

public class CsvReader {
	
private CsvReader() {}
	
	public static List<Node> retrieveNodeFromCsv(String filePath){
		List<Node> customers = new ArrayList<>();
		try(CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] lineInArray;
			reader.readNext();
			while ((lineInArray = reader.readNext()) != null) {
				Node customer = new Node(Integer.valueOf(lineInArray[0]), lineInArray[1], Long.valueOf(lineInArray[2]));
				customers.add(customer);
			}
			return customers;
		} catch (IOException | CsvValidationException e) {
			throw new DataException("Error reading nodes file", e);
		}
	}
	
	public static List<Edge> retrieveEdgesFromCsv(String filePath){
		List<Edge> edges = new ArrayList<>();
		try(CSVReader reader = new CSVReader(new FileReader(filePath))) {
			String[] lineInArray;
			reader.readNext();
			while ((lineInArray = reader.readNext()) != null) {
				Edge edge = new Edge(Long.valueOf(lineInArray[0]), Long.valueOf(lineInArray[1]), Long.valueOf(lineInArray[2]));
				edges.add(edge);
			}
			return edges;
		}catch (IOException | CsvValidationException e) {
			throw new DataException("Error reading edges file", e);
		}
	}

}
