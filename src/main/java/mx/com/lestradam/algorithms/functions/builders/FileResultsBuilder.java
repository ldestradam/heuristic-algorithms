package mx.com.lestradam.algorithms.functions.builders;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;
import mx.com.lestradam.algorithms.utils.CsvWriter;

@Component
public class FileResultsBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(FileResultsBuilder.class);
	
	@Autowired
	private DataSet dataSet;
	
	public void write(final Solution[] solutions, final String path) {
		List<String[]> rows = new ArrayList<>();
		for (int i = 0; i < solutions.length; i++) {
			Solution solution = solutions[i];
			long[] representation = solution.getRepresentation();
			long source = 0;
			long target = 0;
			long distance = 0;
			for (int j = 0; j < representation.length - 1; j++) {
				source = representation[j];
				target = representation[j + 1];
				distance = RoutesOperations.getDistanceNodes(source, target, dataSet.getEdges());
				String[] row = { String.valueOf(source), String.valueOf(target), String.valueOf(distance), "Directed" };
				rows.add(row);
			}
			source = representation[representation.length - 1];
			target = dataSet.getDepot().getId();
			distance = RoutesOperations.getDistanceNodes(source, target, dataSet.getEdges());
			String[] lastRow = { String.valueOf(source), String.valueOf(target), String.valueOf(distance), "Directed" };
			rows.add(lastRow);
			String filename = path + "solution" + i + ".csv";
			logger.info("Creating edge file for solution[{}] in {} ", i, filename);
			CsvWriter.createEdgeFile(filename, rows);
			rows = new ArrayList<>();
		}
	}

}
