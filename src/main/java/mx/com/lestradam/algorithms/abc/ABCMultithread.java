package mx.com.lestradam.algorithms.abc;

import java.util.ArrayList;
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

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.exceptions.AlgorithmExecutionException;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.builders.SolutionSetBuilder;
import mx.com.lestradam.algorithms.functions.fitness.FFArtificialBeeColony;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class ABCMultithread {

	private static Logger logger = LoggerFactory.getLogger(ABCMultithread.class);

	@Autowired
	private ABCParameters params;

	@Autowired
	private SolutionSetBuilder solutionBuilder;

	@Autowired
	private NeighborhoodOperators neighborhood;

	@Autowired
	private FFArtificialBeeColony fitnessFunctions;

	private int numThreads;
	private int foodSourceSize;
	private SolutionSet foodSources;
	private long[] foodSourceLimits;
	private ExecutorService threadPool;

	public SolutionSet execute(int numThreads) {
		this.numThreads = numThreads;
		this.threadPool = Executors.newFixedThreadPool(this.numThreads);
		initial();
		int iteration = 0;
		while (iteration < params.getNumIterations()) {
			LogWriter.printCurrentIteration(foodSources, iteration);
			sendEmployedBees();
			sendOnlooker();
			sendScoutBees();
			iteration++;
		}
		shutdownThreadPool();
		return foodSources;
	}

	private void initial() {
		// Initialize the count for each iteration where the solution (food source) does
		// not show an improvement
		foodSourceSize = params.getFoodSourceSize();
		foodSourceLimits = new long[foodSourceSize];
		long totalobjValue = 0;
		// Randomly generate a set of solutions as initial food sources and evaluate
		// their nectar
		List<long[]> tempSolutions = solutionBuilder.init(foodSourceSize, this.numThreads);
		Solution[] actualSolutions = new Solution[foodSourceSize];
		for (int i = 0; i < foodSourceSize; i++) {
			double objValue = fitnessFunctions.evaluateSolution(tempSolutions.get(i));
			long excess = fitnessFunctions.excess(tempSolutions.get(i));
			totalobjValue += objValue;
			actualSolutions[i] = new Solution(tempSolutions.get(i), objValue, excess);
		}
		foodSources = new SolutionSet(actualSolutions, totalobjValue);
	}

	private void sendEmployedBees() {
		logger.trace("EMPLOYED BEE PHASE");
		List<int[]> splits = BasicOperations.splitRange(this.foodSourceSize, this.numThreads);
		List<Callable<String>> callables = new ArrayList<>();
		for (int[] split : splits) {
			callables.add(sendEmployedBees(split[0], split[1]));
		}
		List<Future<String>> futures = invokeThreads(callables);
		if (logger.isTraceEnabled())
			printThreadResults(futures);
	}

	private Callable<String> sendEmployedBees(int start, int end) {
		return () -> {
			// For each employed bee.(food resource)
			for (int i = start; i <= end; i++) {
				Solution foodSource = foodSources.getSolution(i);
				// Find a new food source in its neighborhood, and evaluate the fitness
				// (neighborhood operator).
				long[] neighbor = neighborhood.randomSwaps(foodSource.getRepresentation());
				// Apply greedy selection on the two food sources.
				double neighborValue = fitnessFunctions.evaluateSolution(neighbor);
				double foodSourceFitness = fitnessFunctions.evaluateSolutionFitness(foodSource.getFitness());
				double neighborFitness = fitnessFunctions.evaluateSolutionFitness(neighborValue);
				if (foodSourceFitness < neighborFitness) {
					long excess = fitnessFunctions.excess(neighbor);
					Solution newNeighbor = new Solution(neighbor, neighborValue, excess);
					foodSources.setSolution(i, newNeighbor);
					foodSourceLimits[i] = 0;
				} else {
					foodSourceLimits[i] = foodSourceLimits[i] + 1;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Food source [{}] limit count: {}", i, foodSourceLimits[i]);
					logger.trace("Food source {} Fitness: {}", foodSource.getRepresentation(), foodSourceFitness);
					logger.trace("Neighbor    {} Fitness: {}", neighbor, neighborFitness);
				}
			}
			return Thread.currentThread().getName() + " Start : " + start + " End: " + end;
		};
	}

	public void sendOnlooker() {
		logger.trace("ONLOOKER BEE PHASE");
		List<int[]> splits = BasicOperations.splitRange(this.foodSourceSize, this.numThreads);
		// For each onlooker
		for (int i = 0; i < params.getOnlookersBees(); i++) {
			logger.trace("ONLOOKER BEE[{}]", i);
			// Calculate the probability for each food source
			double[] probabilities = fitnessFunctions.calculateProbabilities(foodSources.getSolutions());
			List<Callable<String>> callables = new ArrayList<>();
			for (int[] split : splits) {
				callables.add(sendOnlooker(probabilities, split[0], split[1]));
			}
			List<Future<String>> futures = invokeThreads(callables);
			if (logger.isTraceEnabled())
				printThreadResults(futures);
		}
	}

	public Callable<String> sendOnlooker(double[] probabilities, int start, int end) {
		return () -> {
			for (int j = start; j <= end; j++) {
				double rand = Math.random();
				logger.trace("Rand: {} Prob[{}]: {}", rand, j, probabilities[j]);
				if (rand < probabilities[j]) {
					// Send the onlook bee to the food source of the ith employed bee.
					Solution foodSource = foodSources.getSolution(j);
					// Find a new food source in the neighborhood, and evaluate the fitness
					long[] neighbor = neighborhood.randomSwaps(foodSource.getRepresentation());
					double neighborValue = fitnessFunctions.evaluateSolution(neighbor);
					double neighborFitness = fitnessFunctions.evaluateSolutionFitness(neighborValue);
					double foodSourceFitness = fitnessFunctions.evaluateSolutionFitness(foodSource.getFitness());
					// Apply greedy selection on the two food sources.
					if (foodSourceFitness < neighborFitness) {
						foodSourceLimits[j] = 0;
						long excess = fitnessFunctions.excess(neighbor);
						Solution newNeighbor = new Solution(neighbor, neighborValue, excess);
						foodSources.setSolution(j, newNeighbor);
					} else {
						foodSourceLimits[j] = foodSourceLimits[j] + 1;
					}
					if (logger.isTraceEnabled()) {
						logger.trace("Food source [{}] limit count: {}", j, foodSourceLimits[j]);
						logger.trace("Food source {} Fitness: {}", foodSource.getRepresentation(), foodSourceFitness);
						logger.trace("Neighbor    {} Fitness: {}", neighbor, neighborFitness);
					}
				}
			}
			return Thread.currentThread().getName() + " Start : " + start + " End: " + end;
		};
	}

	public void sendScoutBees() {
		logger.trace("SCOUT BEE PHASE");
		List<int[]> splits = BasicOperations.splitRange(this.foodSourceSize, this.numThreads);
		List<Callable<String>> callables = new ArrayList<>();
		for (int[] split : splits) {
			callables.add(sendScoutBees(split[0], split[1]));
		}
		List<Future<String>> futures = invokeThreads(callables);
		if (logger.isTraceEnabled())
			printThreadResults(futures);
	}

	public Callable<String> sendScoutBees(int start, int end) {
		return () -> {
			// If any employed bee becomes scout bee
			for (int i = start; i < end; i++) {
				// Send the scout bee to a randomly produced food source
				if (foodSourceLimits[i] >= params.getImprovedLimit()) {
					long[] solution = solutionBuilder.createSolution();
					double fitness = fitnessFunctions.evaluateSolution(solution);
					long excess = fitnessFunctions.excess(solution);
					Solution newFoodSource = new Solution(solution, fitness, excess);
					foodSources.setSolution(i, newFoodSource);
					foodSourceLimits[i] = 0;
					logger.trace("Food source [{}] limit count reset", i);
				}
			}
			return Thread.currentThread().getName() + " Start : " + start + " End: " + end;
		};
	}

	private void shutdownThreadPool() {
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.trace("Threads were shutting down...");
				threadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error on shutting down threads", e);
		}
	}

	private List<Future<String>> invokeThreads(List<Callable<String>> callables) {
		try {
			return threadPool.invokeAll(callables);
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error sending employed bees", e);
		}
	}

	private void printThreadResults(List<Future<String>> futures) {
		for (Future<String> future : futures) {
			try {
				logger.trace(future.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new AlgorithmExecutionException("Error getting thread results", e);
			}
		}
	}

}
