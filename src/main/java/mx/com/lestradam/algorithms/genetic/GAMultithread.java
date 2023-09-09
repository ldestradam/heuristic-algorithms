package mx.com.lestradam.algorithms.genetic;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.exceptions.AlgorithmExecutionException;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.builders.SolutionSetBuilder;
import mx.com.lestradam.algorithms.functions.fitness.FFGeneticAlgorithm;
import mx.com.lestradam.algorithms.operators.CrossoverOperators;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.operators.SelectionOperators;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class GAMultithread {

	private static Logger logger = LoggerFactory.getLogger(GAMultithread.class);

	private int numThreads;
	private SolutionSet population;
	private ExecutorService threadPool;

	@Autowired
	private GeneticParameters params;

	@Autowired
	private SolutionSetBuilder solutionBuilder;

	@Autowired
	private FFGeneticAlgorithm fitnessFunc;

	@Autowired
	private CrossoverOperators crossover;

	@Autowired
	private NeighborhoodOperators neighborhood;

	public SolutionSet execute(int numThreads) {
		this.numThreads = numThreads;
		this.threadPool = Executors.newFixedThreadPool(this.numThreads);
		// Initialize population
		initial();
		int generation = 0;
		LogWriter.printCurrentIteration(population, generation);
		while (generation < params.getNumGenerations()) {
			// Create temporary population
			Solution[] tmp = createTemporalPopulation();
			// Replace individuals based on fitness
			evaluate(tmp);
			// Increase generation counter
			generation++;
			// Print current generation
			LogWriter.printCurrentIteration(population, generation);
		}
		shutdownThreadPool();
		return population;
	}

	private void initial() {
		logger.debug("Creating initial population...");
		List<long[]> tempSolutions = solutionBuilder.init(params.getPopulationSize(), this.numThreads);
		Solution[] actualSolutions = new Solution[params.getPopulationSize()];
		long totalFitness = 0;
		for (int i = 0; i < params.getPopulationSize(); i++) {
			double fitness = fitnessFunc.evaluateSolution(tempSolutions.get(i));
			long excess = fitnessFunc.excess(tempSolutions.get(i));
			actualSolutions[i] = new Solution(tempSolutions.get(i), fitness, excess);
			totalFitness += fitness;
			if (logger.isDebugEnabled())
				logger.debug("Individual[{}] created: {}", i, actualSolutions[i]);
		}
		population = new SolutionSet(actualSolutions, totalFitness);
	}

	private Solution[] createTemporalPopulation() {
		logger.debug("Creating temporal population...");
		int start = 0;
		int size = params.getPopulationSize() - params.getElitism();
		Solution[] temp = new Solution[size]; 
		List<int[]> splits = BasicOperations.splitRange(size, this.numThreads);
		List<Callable<Solution[]>> callables = splits.stream().map(split -> createIndividuals(split[0], split[1]))
				.collect(Collectors.toList());
		List<Future<Solution[]>> futures = invokeThreads(callables);
		for(Future<Solution[]> future : futures) {
			Solution[] solutions = getThreadResults(future);
			for(int i = 0; i < solutions.length; i++)
				temp[i + start] = solutions[i]; 
			start += solutions.length;
		}
		return temp;
	}

	private Callable<Solution[]> createIndividuals(int start, int end) {
		return () -> {
			int i = 0;
			int size = end - start + 1;
			Solution[] individuals = new Solution[size];
			while (i < size) {
				Solution[] parents = selection();
				Solution[] offsprings = crossover(parents[0], parents[1]);
				individuals[i] = offsprings[0];
				if (i < size - 1)
					individuals[i + 1] = offsprings[1];
				i = i + 2;
			}
			return individuals;
		};
	}

	private Solution[] selection() {
		// Select individuals based on fitness
		logger.debug("Parent selection");
		Solution parent1 = SelectionOperators.inverseRouletteSelection(population.getSolutions());
		Solution parent2 = SelectionOperators.inverseRouletteSelection(population.getSolutions());
		if (logger.isDebugEnabled()) {
			logger.debug("Parent 1: {}", Arrays.toString(parent1.getRepresentation()));
			logger.debug("Parent 2: {}", Arrays.toString(parent2.getRepresentation()));
		}
		return new Solution[] { parent1, parent2 };
	}

	private Solution[] crossover(Solution parent1, Solution parent2) {
		// Apply crossover
		if (params.getCrossoverRate() > Math.random()) {
			// Initialize offspring
			logger.debug("Crossover operation");
			List<long[]> offsprings = crossover.orderCrossover(parent1.getRepresentation(),
					parent2.getRepresentation());
			long[] chromosome1 = offsprings.get(0);
			long[] chromosome2 = offsprings.get(1);
			if (logger.isDebugEnabled()) {
				logger.debug("Offspring 1: {}", Arrays.toString(chromosome1));
				logger.debug("Offspring 2: {}", Arrays.toString(chromosome2));
			}
			// Apply mutation
			logger.debug("Mutation operation");
			if (params.getMutationRate() > Math.random()) {
				chromosome1 = neighborhood.randomSwaps(chromosome1);
				chromosome2 = neighborhood.randomSwaps(chromosome2);
				if (logger.isDebugEnabled()) {
					logger.debug("Offspring mutated 1: {}", Arrays.toString(chromosome1));
					logger.debug("Offspring mutated 2: {}", Arrays.toString(chromosome2));
				}
			}
			// Add offsprings to new population
			Solution offspring1 = new Solution(chromosome1);
			offspring1.setFitness(fitnessFunc.evaluateSolution(chromosome1));
			offspring1.setOvercap(fitnessFunc.excess(chromosome1));
			Solution offspring2 = new Solution(chromosome2);
			offspring2.setFitness(fitnessFunc.evaluateSolution(chromosome2));
			offspring2.setOvercap(fitnessFunc.excess(chromosome2));
			return new Solution[] { offspring1, offspring2 };
		} else {
			// Add parents to new population without applying crossover
			logger.debug("Crossover operation not applied");
			return new Solution[] { parent1, parent2 };
		}
	}

	private void evaluate(Solution[] tempPopulation) {
		// Elitism: Keep the top N best-performing individuals from the current
		// generation
		logger.debug("Elitism: {}", params.getElitism());
		double[] fitnesses = Arrays.stream(population.getSolutions()).mapToDouble(Solution::getFitness).toArray();
		Solution[] newPopulation = new Solution[params.getPopulationSize()];
		for (int i = 0; i < params.getElitism(); i++) {
			int ind = BasicOperations.getNthMinValueIndex(fitnesses, i);
			newPopulation[i] = population.getSolution(ind);
			if (logger.isDebugEnabled()) {
				logger.debug("Best solution[{}] {}", i, newPopulation[i]);
			}
		}
		// Fill the rest of the new population with the remaining offspring
		for (int i = 0; i < tempPopulation.length; i++) {
			newPopulation[params.getElitism() + i] = tempPopulation[i];
		}
		// Set fitness population
		long populationFitness = fitnessFunc.evaluateSolutionSet(newPopulation);
		// Set temporary population as new population
		logger.debug("Setting temporary population as new population");
		population.setSolutions(newPopulation);
		population.setFitness(populationFitness);
	}

	private void shutdownThreadPool() {
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
				logger.debug("Threads were shutting down...");
				threadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error on shutting down threads", e);
		}
	}

	private <T> List<Future<T>> invokeThreads(List<Callable<T>> callables) {
		try {
			return threadPool.invokeAll(callables);
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error sending employed bees", e);
		}
	}

	private <T> T[] getThreadResults(Future<T[]> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new AlgorithmExecutionException("Error getting thread results", e);
		}
	}

}
