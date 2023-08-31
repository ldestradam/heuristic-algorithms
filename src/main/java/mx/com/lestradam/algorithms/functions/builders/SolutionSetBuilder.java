package mx.com.lestradam.algorithms.functions.builders;

import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.exceptions.AlgorithmExecutionException;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;

/**
 * Interface for the creation of solutions To facilitate the exchange of
 * implementation of creation of solutions.
 * 
 * @author leonardo estrada
 *
 */
@Component
public class SolutionSetBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(SolutionSetBuilder.class);

	@Autowired
	@Qualifier("RandomizedGreedy")
	private SolutionBuilder solutionBuilder;

	/**
	 * Create a set of solutions from the list of client nodes, depot and number of
	 * fleets
	 * 
	 * @param setSize Set size
	 * @return Representation of the solution based on the given implementation
	 */
	public List<long[]> init(int setSize) {
		logger.trace("Creating a set of {} solutions", setSize);
		List<long[]> solutions = new ArrayList<>();
		for (int i = 0; i < setSize; i++) {
			long[] solution = createSolution();
			solutions.add(solution);
			logger.trace("Solution {} created", i);
		}
		return solutions;
	}

	/**
	 * Create a set of solutions from the list of client nodes, depot and number of
	 * fleets. The creation of solutions will be divided between the number of
	 * threads specified.
	 * 
	 * @param setSize    Set size
	 * @param numThreads Number of threads
	 * @return Representation of the solution based on the given implementation
	 */
	public List<long[]> init(int setSize, int numThreads) {
		logger.trace("Creating a set of {} solutions with {} threads", setSize, numThreads);
		List<int[]> splits = BasicOperations.splitRange(setSize, numThreads);
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		List<Callable<List<long[]>>> callables = splits.stream().map(split -> createSolutions(split[0], split[1]))
				.collect(Collectors.toList());
		try {
			List<long[]> set = new ArrayList<>();
			List<Future<List<long[]>>> futures = threadPool.invokeAll(callables);
			for (Future<List<long[]>> future : futures) {
				List<long[]> parcialSet = future.get();
				set.addAll(parcialSet);
			}
			threadPool.shutdown();
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS))
				threadPool.shutdownNow();
			return set;
		} catch (InterruptedException | ExecutionException e) {
			throw new AlgorithmExecutionException("Error creating initial solutions", e);
		}
	}

	public Callable<List<long[]>> createSolutions(int start, int end) {
		return () -> {
			List<long[]> solutions = new ArrayList<>();
			for (int i = start; i <= end; i++) {
				solutions.add(solutionBuilder.createSolution());
				logger.trace("Solution {} created", i);
			}
			return solutions;
		};
	}

	/**
	 * Create a solution from the list of client nodes, depot and number of fleets
	 * 
	 * @return Representation of the solution based on the given implementation
	 */
	public long[] createSolution() {
		return solutionBuilder.createSolution();
	}

}
