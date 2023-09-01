package mx.com.lestradam.algorithms.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	private ExecutorService threadPool;
	private SolutionSet population;
	private List<Solution> tempPopulation;
	private int numThreads;

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
		// Initialize population
		initial();
		int generation = 0;
		threadPool = Executors.newFixedThreadPool(this.numThreads);
		LogWriter.printCurrentIteration(population, generation);
		while (generation < params.getNumGenerations()) {
			// Create temporary population
			createTemporalPopulation();
			// Replace individuals based on fitness
			evaluate(tempPopulation);
			// Increase generation counter
			generation++;
			// Print current generation
			LogWriter.printCurrentIteration(population, generation);
		}
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.trace("Threads were shutting down...");
				threadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error on shutting down threads", e);
		}
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

	private void createTemporalPopulation() {
		logger.debug("Creating temporal population...");
		tempPopulation = new ArrayList<>();
		List<Callable<String>> callables = new ArrayList<>();
		for (int i = 0; i < this.numThreads; i++) {
			callables.add(createIndividuals());
		}
		List<Future<String>> futures;
		try {
			futures = threadPool.invokeAll(callables);
			if (logger.isTraceEnabled()) {
				for (Future<String> future : futures) {
					logger.trace(future.get());
				}
			}
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error getting thread results", e);
		} catch (ExecutionException e) {
			throw new AlgorithmExecutionException("Error creating new individuals", e);
		}
	}

	private Callable<String> createIndividuals() {
		return () -> {
			int counter = 0;
			// Loop over current population
			while (isTempPopulationDone()) {
				// Select individuals based on fitness
				Solution[] parents = select();
				// Apply genetic operators
				Solution[] offsprings = recombine(parents[0], parents[1]);
				counter += addIndividuals(offsprings);
			}
			return Thread.currentThread().getName() + " - " + counter + " offsprings added";
		};
	}

	private Solution[] select() {
		// Select parents
		logger.debug("Parent selection");
		Solution parent1 = SelectionOperators.inverseRouletteSelection(population.getSolutions());
		Solution parent2 = SelectionOperators.inverseRouletteSelection(population.getSolutions());
		if (logger.isDebugEnabled()) {
			logger.debug("Parent 1: {}", Arrays.toString(parent1.getRepresentation()));
			logger.debug("Parent 2: {}", Arrays.toString(parent2.getRepresentation()));
		}
		return new Solution[] { parent1, parent2 };
	}

	private Solution[] recombine(Solution parent1, Solution parent2) {
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

	private void evaluate(List<Solution> tempPopulation) {
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
		for (int i = 0; i < tempPopulation.size(); i++) {
			newPopulation[params.getElitism() + i] = tempPopulation.get(i);
		}
		// Set fitness population
		long populationFitness = fitnessFunc.evaluateSolutionSet(newPopulation);
		// Set temporary population as new population
		logger.debug("Setting temporary population as new population");
		population.setSolutions(newPopulation);
		population.setFitness(populationFitness);
	}

	private synchronized boolean isTempPopulationDone() {
		boolean isDone = tempPopulation.size() < (params.getPopulationSize() - params.getElitism());
		logger.trace("Temporal population is done: {}", isDone);
		return isDone;
	}

	private synchronized int addIndividuals(Solution[] offsprings) {
		int counter = 0;
		if (logger.isTraceEnabled())
			logger.trace("Attempting to add 1st offspring: {}", offsprings[0]);
		if (tempPopulation.size() != (params.getPopulationSize() - params.getElitism())) {
			if (logger.isTraceEnabled())
				logger.trace("1st offspring added: {}", offsprings[0]);
			tempPopulation.add(offsprings[0]);
			counter++;
		}
		if (logger.isTraceEnabled())
			logger.trace("Attempting to add 2nd offspring: {}", offsprings[1]);
		if (tempPopulation.size() != (params.getPopulationSize() - params.getElitism())) {
			if (logger.isTraceEnabled())
				logger.trace("2nd offspring added: {}", offsprings[1]);
			tempPopulation.add(offsprings[1]);
			counter++;
		}
		return counter;
	}

}
