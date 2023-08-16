package mx.com.lestradam.algorithms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.basic.StatisticalOperations;

public class LogWriter {

	private static Logger logger = LoggerFactory.getLogger(LogWriter.class);

	private LogWriter() {
		throw new IllegalStateException("Utility class");
	}

	public static void printCurrentIteration(final SolutionSet population, final int generation) {
		Solution[] individuals = population.getSolutions();
		double[] fitness = new double[individuals.length];
		for (int i = 0; i < individuals.length; i++)
			fitness[i] = individuals[i].getFitness();
		int indMax = BasicOperations.getMaxValueIndex(fitness);
		int indMin = BasicOperations.getMinValueIndex(fitness);
		double avg = StatisticalOperations.getAvgValue(fitness);
		logger.info("ITERATION: {} \t MAX: {} \t MIN: {} \t AVG: {}", generation, fitness[indMax], fitness[indMin],
				avg);
		if (logger.isDebugEnabled()) {
			logger.debug("GENERAL FITNESS: {}", population.getFitness());
			printSolutions(individuals);
		}
	}

	public static void printSolutions(final Solution[] solutions) {
		if (logger.isDebugEnabled()) {
			for (int i = 0; i < solutions.length; i++)
				logger.debug("[{}] {}", i, solutions[i]);
		}
	}

	public static void printPopulation(final SolutionSet population, final long duration) {
		logger.info("FINAL RESULTS");
		logger.info("TIME ELAPSED: {} nano seconds", duration);
		logger.info("POPULATION FITNESS: {}", population.getFitness());
		for (Solution individual : population.getSolutions()) {
			logger.info("{}", individual);
		}
	}

	public static void printGeneralParameters(final AlgorithmsParameters params) {
		logger.info("GENERAL PARAMETERS CONFIGURATION");
		logger.info("FLEET CAPACITY: {}", params.getFleetCapacity());
		logger.info("NUM. OF FLEETS: {}", params.getNumFleet());
	}

	public static void printGeneticParameters(final GeneticParameters params) {
		logger.info("GENETIC PARAMETERS CONFIGURATION");
		logger.info("POPULATION SIZE: {}", params.getPopulationSize());
		logger.info("NUM. OF GENERATION: {}", params.getNumGenerations());
		logger.info("CROSSOVER RATE: {}", params.getCrossoverRate());
		logger.info("MUTATION RATE: {}", params.getMutationRate());
	}

	public static void printAbcParameters(final ABCParameters params) {
		logger.info("ABC PARAMETERS CONFIGURATION");
		logger.info("FOOD SOURCE SIZE {}", params.getFoodSourceSize());
		logger.info("IMPROVED LIMIT: {}", params.getImprovedLimit());
		logger.info("NUM. OF ITERATIONS: {}", params.getNumIterations());
		logger.info("ONLOOKERS BEES: {}", params.getOnlookersBees());
	}

	public static void printPsoParameters(final PSOParameters params) {
		logger.info("PSO PARAMETERS CONFIGURATION");
		logger.info("INERTIA {}", params.getInertia());
		logger.info("ITERATIONS {}", params.getNumIterations());
		logger.info("PARTICLES {}", params.getNumParticles());
		logger.info("ACCELERATION CONSTANT 1 {}", params.getAccelerationC1());
		logger.info("ACCELERATION CONSTANT 2 {}", params.getAccelerationC2());
	}
}
