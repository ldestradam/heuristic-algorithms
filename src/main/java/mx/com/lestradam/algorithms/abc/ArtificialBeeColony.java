package mx.com.lestradam.algorithms.abc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.builders.SolutionSetBuilder;
import mx.com.lestradam.algorithms.functions.fitness.FitnessFunction;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.operators.SelectionOperators;

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
	@Qualifier("FFArtificialBeeColony")
	private FitnessFunction fitnessFunc;
	
	private int foodSourceSize;
	private SolutionSet foodSources;
	private long[] foodSourceLimits;
	
	public void initial() {
		// Initialize the count for each iteration where the solution (food source) does not show an improvement
		foodSourceSize = params.getFoodSourceSize();
		foodSourceLimits = new long[foodSourceSize];
		long totalFitness = 0;
		// Randomly generate a set of solutions as initial food sources and evaluate their nectar (fitness)
		List<long[]> tempSolutions = solutionBuilder.init(foodSourceSize);
		Solution[] actualSolutions = new Solution[foodSourceSize];
		for(int i = 0; i < foodSourceSize; i++) {
			long fitness = fitnessFunc.evaluateSolution(tempSolutions.get(i));
			totalFitness += fitness;
			actualSolutions[i] = new Solution(tempSolutions.get(i), fitness);
		}
		foodSources = new SolutionSet(actualSolutions, totalFitness);
	}
	
	public void sendEmployedBees() {
		// For each employed bee.(food resource)
		for(int i = 0; i < foodSourceSize; i++){
			long[] foodSource = foodSources.getSolution(i).getRepresentation();
			// Find a new food source in its neighborhood, and evaluate the fitness.
			long[] neighbor = neighborhood.randomSwaps(foodSource);
			long foodSourceFitness = fitnessFunc.evaluateSolution(foodSource);
			long neighborFitness = fitnessFunc.evaluateSolution(neighbor);
			// Apply greedy selection on the two food sources.
			if(neighborFitness > foodSourceFitness){
				Solution fooSource = new Solution(neighbor, neighborFitness);
				foodSources.setSolution(i, fooSource);
				foodSourceLimits[i] = 0;
			} else {
				foodSourceLimits[i] = foodSourceLimits[i] + 1;
			}
			if(logger.isTraceEnabled()) {
				logger.trace("EMPLOYED BEE PHASE");
				logger.trace("Food source [{}]  limit count: {}", i, foodSourceLimits[i]);
				logger.trace("Food source [{}] {} Fitness: {}", i, foodSource, foodSourceFitness);
				logger.trace("Neighbor food source [{}] {} Fitness: {}", i, neighbor, neighborFitness);
			}
		}
	}
	
	public void sendOnlooker() {
		List<List<Solution>> foodSourcesNeighborhood = new ArrayList<>();
		// For each onlooker
		for(int i = 0; i < foodSourceSize; i++)
			foodSourcesNeighborhood.add(new ArrayList<>());
		for(int i = 0; i < foodSourceSize; i++){
			// Select a food source using the fitness-based roulette wheel selection method.
			int index = SelectionOperators.rouletteSelectionIndex(foodSources.getSolutions(), foodSources.getFitness());
			//Apply a neighborhood operator on food source i
			long[] xi = foodSources.getSolution(index).getRepresentation();
			long[] xt = neighborhood.randomSwaps(xi);
			long fitnessXt = fitnessFunc.evaluateSolution(xt);
			Solution foodSource = new Solution(xt, fitnessXt);
			foodSourcesNeighborhood.get(index).add(foodSource);
		}		
		//For each food source i and its neighborhood is not empty
		for(int i = 0; i < foodSourceSize; i++){
			if(!foodSourcesNeighborhood.get(i).isEmpty()){
				Solution bestNeighbor = getMaxNeighborFoodSource(foodSourcesNeighborhood.get(i));
				Solution foodSource = foodSources.getSolution(i);
				if (foodSource.getFitness() < bestNeighbor.getFitness()) {
					foodSources.setSolution(i, bestNeighbor);
					foodSourceLimits[i] = 0;
				}else {
					foodSourceLimits[i] = foodSourceLimits[i] + 1;
				}
				if(logger.isTraceEnabled()) {
					logger.trace("ONLOOKER BEE PHASE");
					logger.trace("Food source [{}]  limit count: {}", i, foodSourceLimits[i]);
					logger.trace("Food source [{}] {}", i, foodSource);
					logger.trace("Best neighbor food source [{}] {}", i, bestNeighbor);
					printNeighborFoodSource(foodSourcesNeighborhood.get(i), i);
				}
			}
		}
	}
	
	public void sendScoutBees() {
		//If any employed bee becomes scout bee
		for(int i = 0; i < foodSourceSize; i++){
			// Send the scout bee to a randomly produced food source
			if(foodSourceLimits[i] == params.getImprovedLimit()){
				long[] solution = solutionBuilder.createSolution();
				long fitness = fitnessFunc.evaluateSolution(solution);
				Solution randFoodSource = new Solution(solution, fitness);
				foodSources.setSolution(i, randFoodSource);
				foodSourceLimits[i] = 0;
				if(logger.isTraceEnabled()) {
					logger.trace("SCOUT BEE PHASE");
					logger.trace("Food source [{}] limit count reset", i);
					logger.trace("New random food source [{}] {}", i, randFoodSource);
				}
			}
		}
	}
	
	public SolutionSet execute() {
		initial();
		int iteration = 0;		
		while(iteration < params.getNumIterations()) {
			if(logger.isDebugEnabled())
				printCurrentIteration(foodSources, iteration);
			sendEmployedBees();
			sendOnlooker();
			sendScoutBees();
			iteration++;
		}
		return foodSources;
	}
	
	private void printCurrentIteration(SolutionSet population, int iteration) {
		logger.debug("CURRENT ITERATION: {}", iteration);
		logger.debug("ITERATION FITNESS: {}", population.getFitness());
		for(Solution foodSource : population.getSolutions())
			logger.debug("{}", foodSource);
	}
	
	private void printNeighborFoodSource(List<Solution> neighbors, int foodSource) {
		for(Solution neighbor : neighbors)
			logger.trace("Neighbor food source [{}] {}", foodSource, neighbor);
	}
	
	private Solution getMaxNeighborFoodSource(List<Solution> foodSources) {
		long[] fitness = foodSources.stream().mapToLong(Solution::getFitness).toArray();
		int minIndex = BasicOperations.getMaxValueIndex(fitness);
		return foodSources.get(minIndex);
	}

}
