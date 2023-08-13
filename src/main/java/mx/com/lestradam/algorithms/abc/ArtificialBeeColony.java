package mx.com.lestradam.algorithms.abc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.functions.builders.SolutionSetBuilder;
import mx.com.lestradam.algorithms.functions.fitness.FFArtificialBeeColony;
import mx.com.lestradam.algorithms.functions.fitness.ObjectiveFunction;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class ArtificialBeeColony {

	private static Logger logger = LoggerFactory.getLogger(ArtificialBeeColony.class);

	@Autowired
	private ABCParameters params;

	@Autowired
	private SolutionSetBuilder solutionBuilder;

	@Autowired
	private NeighborhoodOperators neighborhood;

	@Autowired
	private ObjectiveFunction objFunction;

	@Autowired
	private FFArtificialBeeColony fitnessFunctions;

	private int foodSourceSize;
	private SolutionSet foodSources;
	private long[] foodSourceLimits;

	public void initial() {
		// Initialize the count for each iteration where the solution (food source) does
		// not show an improvement
		foodSourceSize = params.getFoodSourceSize();
		foodSourceLimits = new long[foodSourceSize];
		long totalobjValue = 0;
		// Randomly generate a set of solutions as initial food sources and evaluate
		// their nectar
		List<long[]> tempSolutions = solutionBuilder.init(foodSourceSize);
		Solution[] actualSolutions = new Solution[foodSourceSize];
		for (int i = 0; i < foodSourceSize; i++) {
			long objValue = objFunction.evaluate(tempSolutions.get(i));
			totalobjValue += objValue;
			actualSolutions[i] = new Solution(tempSolutions.get(i), objValue);
		}
		foodSources = new SolutionSet(actualSolutions, totalobjValue);
	}

	public void sendEmployedBees() {
		// For each employed bee.(food resource)
		for (int i = 0; i < foodSourceSize; i++) {
			Solution foodSource = foodSources.getSolution(i);
			// Find a new food source in its neighborhood, and evaluate the fitness
			// (neighborhood operator).
			long[] neighbor = neighborhood.randomSwaps(foodSource.getRepresentation());
			// Apply greedy selection on the two food sources.
			long neighborValue = objFunction.evaluate(neighbor);
			double foodSourceFitness = fitnessFunctions.evaluateSolutionFitness(foodSource.getFitness());
			double neighborFitness = fitnessFunctions.evaluateSolutionFitness(neighborValue);
			if (foodSourceFitness < neighborFitness) {
				Solution newNeighbor = new Solution(neighbor, neighborValue);
				foodSources.setSolution(i, newNeighbor);
				foodSourceLimits[i] = 0;
			} else {
				foodSourceLimits[i] = foodSourceLimits[i] + 1;
			}
			if (logger.isTraceEnabled()) {
				logger.trace("EMPLOYED BEE PHASE");
				logger.trace("Food source [{}]  limit count: {}", i, foodSourceLimits[i]);
				logger.trace("Food source [{}] {} Obj: {} Fitness: {}", i, foodSource.getRepresentation(),
						foodSource.getFitness(), foodSourceFitness);
				logger.trace("Neighbor food source [{}] {} Obj: {} Fitness: {}", i, neighbor, neighborValue,
						neighborFitness);
				LogWriter.printSolutions(foodSources.getSolutions());
			}
		}
	}

	public void sendOnlooker() {
		// Calculate the probability for each food source
		double[] probabilities = fitnessFunctions.calculateProbabilities(foodSources.getSolutions());
		// For each onlooker
		int i = 0;
		int t = 0;
		while (t < foodSourceSize) {
			double rand = Math.random();
			if (logger.isTraceEnabled()) {
				logger.trace("ONLOOKER BEE PHASE");
				logger.trace("Rand: {}", rand);
				logger.trace("Probability: {}", probabilities[i]);
			}
			if (rand < probabilities[i]) {
				t += 1;
				// Send the onlook bee to the food source of the ith employed bee.
				Solution foodSource = foodSources.getSolution(i);
				// Find a new food source in the neighborhood, and evaluate the fitness
				long[] neighbor = neighborhood.randomSwaps(foodSource.getRepresentation());
				long neighborValue = objFunction.evaluate(neighbor);
				double neighborFitness = fitnessFunctions.evaluateSolutionFitness(neighborValue);
				double foodSourceFitness = fitnessFunctions.evaluateSolutionFitness(foodSource.getFitness());
				// Apply greedy selection on the two food sources.
				if (foodSourceFitness < neighborFitness) {
					foodSourceLimits[i] = 0;
					Solution newNeighbor = new Solution(neighbor, neighborValue);
					foodSources.setSolution(i, newNeighbor);

				} else {
					foodSourceLimits[i] = foodSourceLimits[i] + 1;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("ONLOOKER BEE PHASE");
					logger.trace("Food source [{}]  limit count: {}", i, foodSourceLimits[i]);
					logger.trace("Food source [{}] {} Obj: {} Fitness: {}", i, foodSource.getRepresentation(),
							foodSource.getFitness(), foodSourceFitness);
					logger.trace("Neighbor food source [{}] {} Obj: {} Fitness: {}", i, neighbor, neighborValue,
							neighborFitness);
					LogWriter.printSolutions(foodSources.getSolutions());
				}
			}
			i += 1;
			i = i % foodSourceSize;
		}
	}

	public void sendScoutBees() {
		// If any employed bee becomes scout bee
		for (int i = 0; i < foodSourceSize; i++) {
			// Send the scout bee to a randomly produced food source
			if (foodSourceLimits[i] >= params.getImprovedLimit()) {
				long[] solution = solutionBuilder.createSolution();
				long fitness = objFunction.evaluate(solution);
				Solution newFoodSource = new Solution(solution, fitness);
				foodSources.setSolution(i, newFoodSource);
				foodSourceLimits[i] = 0;
				if (logger.isTraceEnabled()) {
					logger.trace("SCOUT BEE PHASE");
					logger.trace("Food source [{}] limit count reset", i);
					logger.trace("New food source [{}] {}", i, newFoodSource);
					LogWriter.printSolutions(foodSources.getSolutions());
				}
			}
		}
	}

	public SolutionSet execute() {
		initial();
		int iteration = 0;
		while (iteration < params.getNumIterations()) {
			LogWriter.printCurrentIteration(foodSources, iteration);
			sendEmployedBees();
			sendOnlooker();
			sendScoutBees();
			iteration++;
		}
		return foodSources;
	}

}
