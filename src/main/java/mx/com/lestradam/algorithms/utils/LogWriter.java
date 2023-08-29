package mx.com.lestradam.algorithms.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.PSOSolution;
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
		double[] fitnesses = new double[individuals.length];
		for (int i = 0; i < individuals.length; i++)
			fitnesses[i] = individuals[i].getFitness();
		int indMax = BasicOperations.getMaxValueIndex(fitnesses);
		int indMin = BasicOperations.getMinValueIndex(fitnesses);
		double avg = StatisticalOperations.getAvgValue(fitnesses);
		logger.info("ITERATION: {} \t MAX: {} \t MIN: {} \t AVG: {}", generation, fitnesses[indMax], fitnesses[indMin],
				avg);
		if (logger.isDebugEnabled()) {
			logger.debug("GENERAL FITNESS: {}", population.getFitness());
			printSolutions(individuals);
		}
	}
	
	public static void printCurrentIterationPso(final List<PSOSolution> particles, final PSOSolution gBestPaticle, final int iteration) {
		double[] fitnesses = particles.stream().mapToDouble(PSOSolution::getFitness).toArray();
		int indMax = BasicOperations.getMaxValueIndex(fitnesses);
		int indMin = BasicOperations.getMinValueIndex(fitnesses);
		double avg = StatisticalOperations.getAvgValue(fitnesses);
		logger.info("ITERATION: {} \t MAX: {} \t MIN: {} \t AVG: {} \t BEST: {}", iteration, fitnesses[indMax], fitnesses[indMin],
				avg, gBestPaticle.getFitness());
		if (logger.isDebugEnabled()) {
			logger.debug("BEST PARTICLE: {}", gBestPaticle);
			for(int i = 0; i < particles.size(); i++) {
				logger.debug("[{}] {}", i, particles.get(i));
			}
		}
	}

	public static void printSolutions(final Solution[] solutions) {
		if (logger.isDebugEnabled()) {
			for (int i = 0; i < solutions.length; i++)
				logger.debug("[{}] {}", i, solutions[i]);
		}
	}

	public static void printPopulation(final SolutionSet population, final long duration) {
		Solution[] solutions = population.getSolutions();
		double[] fitnesses = new double[solutions.length];
		for (int i = 0; i < solutions.length; i++) {
			fitnesses[i] = solutions[i].getFitness();
		}
		int bestSolution = BasicOperations.getMinValueIndex(fitnesses);
		logger.info("FINAL RESULTS");
		logger.info("TIME ELAPSED: {} nano seconds", duration);
		logger.info("BEST SOLUTION INDEX: {} BEST FITNESS: {} - POPULATION FITNESS: {}", bestSolution, fitnesses[bestSolution], population.getFitness());
		for (int i = 0; i < solutions.length; i++) {
			logger.info("[{}] - {}", i, solutions[i]);
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
		logger.info("ELITISM: {}", params.getElitism());
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
