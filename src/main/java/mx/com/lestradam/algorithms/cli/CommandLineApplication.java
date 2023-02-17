package mx.com.lestradam.algorithms.cli;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class CommandLineApplication {
	
	private static Logger logger = LoggerFactory.getLogger(CommandLineApplication.class);
	private static final String ALGO_KEY = "algorithm";
	private static final String FILE_PATH = "file-path";
	private static final String ALGO_VALUE_1 = "genetic";
	private static final String ALGO_VALUE_2 = "abc";
	private static final String ALGO_VALUE_3 = "pso";
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
		if (algo.equals(ALGO_VALUE_1)) {
			executeGeneticAlgorithm();
		} else if(algo.equals(ALGO_VALUE_2)){
			executeAbcAlgorithm();
		} else if (algo.equals(ALGO_VALUE_3)) {
			executePsoAlgorithm();
		} else {
			throw new DataException("Invalid value for parameter: " + ALGO_KEY + ", possible values [" + ALGO_VALUE_1 + "|" + ALGO_VALUE_2 + "|" + ALGO_VALUE_3  +"]");
		}
	}
	
	private void executePsoAlgorithm() {
		printGeneralParameters();
		printPsoParameters();
		long startTime = System.nanoTime();
		SolutionSet solutions = pso.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (checkArgumentKey(FILE_PATH)) 
			writeResults(solutions.getSolutions());
		printPopulation(solutions, duration);
	}
	
	private void executeAbcAlgorithm() {
		printGeneralParameters();
		printAbcParameters();
		long startTime = System.nanoTime();
		SolutionSet solutions = abc.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (checkArgumentKey(FILE_PATH)) 
			writeResults(solutions.getSolutions());
		printPopulation(solutions, duration);
	}
	
	private void executeGeneticAlgorithm() {
		printGeneralParameters();
		printGeneticParameters();
		long startTime = System.nanoTime();
		SolutionSet solutions = genetic.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		if (checkArgumentKey(FILE_PATH)) 
			writeResults(solutions.getSolutions());
		printPopulation(solutions, duration);
	}
	
	private void printGeneralParameters() {
		logger.info("GENERAL PARAMETERS CONFIGURATION");
		logger.info("FLEET CAPACITY: {}", generalParams.getFleetCapacity());
		logger.info("NUM. OF FLEETS: {}", generalParams.getNumFleet());
	}
	
	private void printGeneticParameters() {
		logger.info("GENETIC PARAMETERS CONFIGURATION");
		logger.info("POPULATION SIZE: {}", geneticParams.getPopulationSize());		
		logger.info("NUM. OF GENERATION: {}", geneticParams.getNumGenerations());
		logger.info("CROSSOVER RATE: {}", geneticParams.getCrossoverRate());		
		logger.info("MUTATION RATE: {}", geneticParams.getMutationRate());
	}
	
	private void printAbcParameters() {
		logger.info("ABC PARAMETERS CONFIGURATION");
		logger.info("FOOD SOURCE SIZE {}", abcParams.getFoodSourceSize());
		logger.info("IMPROVED LIMIT: {}", abcParams.getImprovedLimit());
		logger.info("NUM. OF ITERATIONS: {}", abcParams.getNumIterations());
		logger.info("ONLOOKERS BEES: {}", abcParams.getOnlookersBees());
	}
	
	private void printPsoParameters() {
		logger.info("PSO PARAMETERS CONFIGURATION");
		logger.info("INERTIA {}", psoParams.getInertia());
		logger.info("ITERATIONS {}", psoParams.getNumIterations());
		logger.info("PARTICLES {}", psoParams.getNumParticles());
		logger.info("ACCELERATION CONSTANT 1 {}", psoParams.getAccelerationC1());
		logger.info("ACCELERATION CONSTANT 2 {}", psoParams.getAccelerationC2());
	}
	
	private void printPopulation(SolutionSet population, long duration) {
		logger.info("FINAL RESULTS");
		logger.info("TIME ELAPSED: {} nano seconds", duration);
		logger.info("POPULATION FITNESS: {}", population.getFitness());
		for(Solution individual : population.getSolutions()) {
			logger.info("{}", individual);
		}
	}
	
	private boolean checkArgumentKey(String key) {
		for(int i = 0; i < arguments.length; i++) {
			if(arguments[i].indexOf(key) == 0)
				return true;
		}
		return false;
	}
	
	private String retrieveArgumentValue(String key) {
		for(int i = 0; i < arguments.length; i++) {
			if(arguments[i].indexOf(key) == 0) {
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
				String[] row = {String.valueOf(source), String.valueOf(target), String.valueOf(distance), "Directed"};
				rows.add(row);
			}
			source = representation[representation.length - 1];
			target = dataSet.getDepot().getId();
			distance = RoutesOperations.getDistanceNodes(source, target, dataSet.getEdges());
			String[] lastRow = {String.valueOf(source), String.valueOf(target), String.valueOf(distance), "Directed"};
			rows.add(lastRow);
			CsvWriter.createEdgeFile(retrieveArgumentValue(FILE_PATH) + "solution" + i + ".csv", rows);
			rows = new ArrayList<>();
		}
	}
	
}
