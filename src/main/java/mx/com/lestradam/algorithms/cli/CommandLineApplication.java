package mx.com.lestradam.algorithms.cli;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.abc.ArtificialBeeColony;
import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.exceptions.DataException;
import mx.com.lestradam.algorithms.functions.builders.FileResultsBuilder;
import mx.com.lestradam.algorithms.genetic.GAPrincipal;
import mx.com.lestradam.algorithms.pso.ParticleSwarmOptimization;
import mx.com.lestradam.algorithms.utils.ArgumentsRetriever;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class CommandLineApplication {

	private static final String ALGORITHM = "algorithm";
	private static final String FILE_PATH = "file-path";
	private static final String THREADS = "threads";
	private static final String ALGO_GA = "genetic";
	private static final String ALGO_ABC = "abc";
	private static final String ALGO_PSO = "pso";
	private static final List<String> ALGORITHMS = Arrays.asList(ALGO_GA, ALGO_ABC, ALGO_PSO);

	private String[] arguments;

	@Autowired
	private FileResultsBuilder results;

	@Autowired
	private GAPrincipal ga;

	@Autowired
	private ArtificialBeeColony abc;

	@Autowired
	private ParticleSwarmOptimization pso;

	@Autowired
	private AlgorithmsParameters generalParams;

	@Autowired
	private ABCParameters abcParams;

	@Autowired
	private PSOParameters psoParams;

	public void execute(String[] arguments) {
		this.arguments = arguments;
		String algorithm = getAlgorithm();
		LogWriter.printGeneralParameters(generalParams);
		if (algorithm.equals(ALGO_GA)) {
			SolutionSet solutions = executeGa();
			if (ArgumentsRetriever.checkArgumentKey(this.arguments, FILE_PATH))
				results.write(solutions.getSolutions(),
						ArgumentsRetriever.retrieveArgumentValue(this.arguments, FILE_PATH));
		} else if (algorithm.equals(ALGO_ABC)) {
			executeAbcAlgorithm();
		} else {
			executePsoAlgorithm();
		}
	}
	
	private String getAlgorithm() {
		if (!ArgumentsRetriever.checkArgumentKey(this.arguments, ALGORITHM))
			throw new DataException("Missing parameter: " + ALGORITHM);
		String algorithm = ArgumentsRetriever.retrieveArgumentValue(this.arguments, ALGORITHM);
		if (!ALGORITHMS.contains(algorithm)) {
			throw new DataException("Invalid value for parameter: " + ALGORITHM + ", possible values [" + ALGO_GA + "|"
					+ ALGO_ABC + "|" + ALGO_PSO + "]");
		}
		return algorithm;
	}

	private SolutionSet executeGa() {
		if (!ArgumentsRetriever.checkArgumentKey(this.arguments, THREADS)) {
			return ga.run();
		} else {
			int threads = Integer.parseInt(ArgumentsRetriever.retrieveArgumentValue(this.arguments, THREADS));
			return ga.run(threads);
		}
	}

	private void executePsoAlgorithm() {
		LogWriter.printPsoParameters(psoParams);
		long startTime = System.nanoTime();
		SolutionSet solutions = pso.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (ArgumentsRetriever.checkArgumentKey(this.arguments, FILE_PATH))
			results.write(solutions.getSolutions(),
					ArgumentsRetriever.retrieveArgumentValue(this.arguments, FILE_PATH));
		LogWriter.printPopulation(solutions, duration);
	}

	private void executeAbcAlgorithm() {
		LogWriter.printAbcParameters(abcParams);
		long startTime = System.nanoTime();
		SolutionSet solutions = abc.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (ArgumentsRetriever.checkArgumentKey(this.arguments, FILE_PATH))
			results.write(solutions.getSolutions(),
					ArgumentsRetriever.retrieveArgumentValue(this.arguments, FILE_PATH));
		LogWriter.printPopulation(solutions, duration);
	}

}
