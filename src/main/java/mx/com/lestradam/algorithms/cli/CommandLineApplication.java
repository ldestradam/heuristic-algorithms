package mx.com.lestradam.algorithms.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.abc.ArtificialBeeColony;
import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.exceptions.DataException;
import mx.com.lestradam.algorithms.genetic.GeneticAlgorithm;

@Component
public class CommandLineApplication {
	
	private static Logger logger = LoggerFactory.getLogger(CommandLineApplication.class);
	private static final String ALGO_KEY = "algorithm";
	private static final String ALGO_VALUE_1 = "genetic";
	private static final String ALGO_VALUE_2 = "abc";
	private static final String SEPARATOR = ":";
	private String[] arguments;
	
	@Autowired
	private GeneticAlgorithm genetic;
	
	@Autowired
	private ArtificialBeeColony abc;
	
	@Autowired
	private AlgorithmsParameters generalParams;
	
	@Autowired
	private GeneticParameters geneticParams;
	
	@Autowired
	private ABCParameters abcParams;
	
	public void execute(String[] arguments) {
		this.arguments = arguments;
		if(!checkArgumentKey(ALGO_KEY)) 
			throw new DataException("Missing parameter: " + ALGO_KEY);
		String algo = retrieveArgumentValue(ALGO_KEY);
		if(!algo.equals(ALGO_VALUE_1) && !algo.equals(ALGO_VALUE_2))
			throw new DataException("Invalid value for parameter: " + ALGO_KEY + ", possible values [" + ALGO_VALUE_1 + "|" + ALGO_VALUE_2 + "]");
		else if(algo.equals(ALGO_VALUE_1)) {
			executeGeneticAlgorithm();
		}else{
			executeAbcAlgorithm();
		}
	}
	
	private void executeAbcAlgorithm() {
		printGeneralParameters();
		printAbcParameters();
		SolutionSet population = abc.execute();
		printPopulation(population);
	}
	
	private void executeGeneticAlgorithm() {
		printGeneralParameters();
		printGeneticParameters();
		SolutionSet population = genetic.execute();
		printPopulation(population);
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
	
	private void printPopulation(SolutionSet population) {
		logger.info("FINAL RESULTS");
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
	
}
