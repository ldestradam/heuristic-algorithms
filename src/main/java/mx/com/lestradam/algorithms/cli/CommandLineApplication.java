package mx.com.lestradam.algorithms.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.abc.ArtificialBeeColony;
import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.exceptions.DataException;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;
import mx.com.lestradam.algorithms.genetic.GeneticAlgorithm;
import mx.com.lestradam.algorithms.pso.ParticleSwarmOptimization;
import mx.com.lestradam.algorithms.utils.CsvWriter;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class CommandLineApplication {

	private static final String ALGO_KEY = "algorithm";
	private static final String FILE_PATH = "file-path";
	private static final String ALGO_VALUE_1 = "genetic";
	private static final String ALGO_VALUE_2 = "abc";
	private static final String ALGO_VALUE_3 = "pso";
	private static final List<String> ALGO_VALUES = Arrays.asList(ALGO_VALUE_1, ALGO_VALUE_2, ALGO_VALUE_3);
	private static final String SEPARATOR = ":";

	private String[] arguments;

	@Autowired
	private GeneticAlgorithm genetic;

	@Autowired
	private ArtificialBeeColony abc;

	@Autowired
	private ParticleSwarmOptimization pso;

	@Autowired
	private AlgorithmsParameters generalParams;

	@Autowired
	private GeneticParameters geneticParams;

	@Autowired
	private ABCParameters abcParams;

	@Autowired
	private PSOParameters psoParams;

	@Autowired
	private DataSet dataSet;

	public void execute(String[] arguments) {
		this.arguments = arguments;

		if (!checkArgumentKey(ALGO_KEY))
			throw new DataException("Missing parameter: " + ALGO_KEY);
		String algo = retrieveArgumentValue(ALGO_KEY);
		if (!ALGO_VALUES.contains(algo)) {
			throw new DataException("Invalid value for parameter: " + ALGO_KEY + ", possible values [" + ALGO_VALUE_1
					+ "|" + ALGO_VALUE_2 + "|" + ALGO_VALUE_3 + "]");
		}
		LogWriter.printGeneralParameters(generalParams);
		if (algo.equals(ALGO_VALUE_1)) {
			executeGeneticAlgorithm();
		} else if (algo.equals(ALGO_VALUE_2)) {
			executeAbcAlgorithm();
		} else {
			executePsoAlgorithm();
		}
	}

	private void executePsoAlgorithm() {
		LogWriter.printPsoParameters(psoParams);
		long startTime = System.nanoTime();
		SolutionSet solutions = pso.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (checkArgumentKey(FILE_PATH))
			writeResults(solutions.getSolutions());
		LogWriter.printPopulation(solutions, duration);
	}

	private void executeAbcAlgorithm() {
		LogWriter.printAbcParameters(abcParams);
		long startTime = System.nanoTime();
		SolutionSet solutions = abc.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (checkArgumentKey(FILE_PATH))
			writeResults(solutions.getSolutions());
		LogWriter.printPopulation(solutions, duration);
	}

	private void executeGeneticAlgorithm() {
		LogWriter.printGeneticParameters(geneticParams);
		long startTime = System.nanoTime();
		SolutionSet solutions = genetic.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (checkArgumentKey(FILE_PATH))
			writeResults(solutions.getSolutions());
		LogWriter.printPopulation(solutions, duration);
	}

	private boolean checkArgumentKey(String key) {
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].indexOf(key) == 0)
				return true;
		}
		return false;
	}

	private String retrieveArgumentValue(String key) {
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].indexOf(key) == 0) {
				String arg = arguments[i];
				return arg.substring(arg.indexOf(SEPARATOR) + 1, arg.length());
			}
		}
		throw new DataException("Missing value for parameter: " + key);
	}

	private void writeResults(final Solution[] solutions) {
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
			CsvWriter.createEdgeFile(retrieveArgumentValue(FILE_PATH) + "solution" + i + ".csv", rows);
			rows = new ArrayList<>();
		}
	}

}
