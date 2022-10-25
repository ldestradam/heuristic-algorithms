package mx.com.lestradam.algorithms.abc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.ABCParameters;
import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.functions.generation.BasicOperations;
import mx.com.lestradam.algorithms.functions.generation.PopulationOperations;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.operators.SelectionOperators;

@Component
public class ArtificialBeeColony {
	
	private static Logger logger = LoggerFactory.getLogger(ArtificialBeeColony.class);
	
	@Autowired
	private ABCParameters params;
	
	@Autowired
	private PopulationOperations populationOps;
	
	@Autowired
	private NeighborhoodOperators neighborhood;
	
	private int foodSourceSize;
	private Population foodSources;
	private long[] foodSourceLimits;
	
	public void initial() {
		// Randomly generate a set of solutions as initial food sources and evaluate their nectar (fitness)
		foodSources = populationOps.initPopulation(foodSourceSize);
		// Initialize the count for each iteration where the solution (food source) does not show an improvement
		foodSourceSize = params.getFoodSourceSize();
		foodSourceLimits = new long[foodSourceSize];
	}
	
	public void sendEmployedBees() {
		// For each employed bee.(food resource)
		for(int i = 0; i < foodSourceSize; i++){
			long[] foodSource = foodSources.getIndividual(i).getChromosome();
			// Find a new food source in its neighborhood, and evaluate the fitness.
			long[] neighbor = neighborhood.randomSwaps(foodSource);
			long fitnessXi = populationOps.getIndividualFitness(foodSource);
			long fitnessXt = populationOps.getIndividualFitness(neighbor);
			// Apply greedy selection on the two food sources.
			if(fitnessXt > fitnessXi){
				Individual fooSource = new Individual(neighbor, fitnessXt);
				foodSources.setIndividual(i, fooSource);
				foodSourceLimits[i] = 0;
			} else {
				foodSourceLimits[i] = foodSourceLimits[i] + 1;
			}
			if(logger.isTraceEnabled()) {
				logger.trace("Food source [{}]  limit count: {}", i, foodSourceLimits[i]);
				logger.trace("Food source [{}]  Fitness: {} \n {}", i, fitnessXi, foodSource);
				logger.trace("Neighbor food source [{}]  Fitness: {} \n {}", i, fitnessXt, neighbor);
			}
		}
	}
	
	public void sendOnlooker() {
		List<List<Individual>> foodSourcesNeighborhood = new ArrayList<>();
		// For each onlooker
		for(int i = 0; i < foodSourceSize; i++)
			foodSourcesNeighborhood.add(new ArrayList<>());
		for(int i = 0; i < foodSourceSize; i++){
			// Select a food source using the fitness-based roulette wheel selection method.
			int index = SelectionOperators.rouletteSelectionIndex(foodSources.getIndividuals(), foodSources.getPopulationFitness());
			//Apply a neighborhood operator on food source i
			long[] xi = neighborhood.randomSwaps(foodSources.getIndividual(index).getChromosome());
			long[] xt = neighborhood.randomSwaps(xi);
			long fitnessXt = populationOps.getIndividualFitness(xt);
			Individual foodSource = new Individual(xt, fitnessXt);
			foodSourcesNeighborhood.get(index).add(foodSource);
		}
		//For each food source i and its neighborhood is not empty
		for(int i = 0; i < foodSourceSize; i++){
			if(!foodSourcesNeighborhood.get(i).isEmpty()){
				Individual neighbor = getMaxNeighborFoodSource(foodSourcesNeighborhood.get(i));
				Individual foodSource = foodSources.getIndividual(i);
				if (foodSource.getFitness() < neighbor.getFitness()) {
					foodSources.setIndividual(i, neighbor);
					foodSourceLimits[i] = 0;
				}else {
					foodSourceLimits[i] = foodSourceLimits[i] + 1;
				}
			}
		}
	}
	
	public void sendScoutBees() {
		//If any employed bee becomes scout bee
		for(int i = 0; i < foodSourceSize; i++){
			// Send the scout bee to a randomly produced food source
			if(foodSourceLimits[i] == params.getImprovedLimit()){
				Individual randFoodSource = populationOps.createIndividual();
				foodSources.setIndividual(i, randFoodSource);
			}
		}
	}
	
	public Population execute() {
		initial();
		int iteration = 0;
		while(iteration < params.getNumIterations()) {
			sendEmployedBees();
			sendOnlooker();
			sendScoutBees();
			iteration++;
		}
		return foodSources;
	}
	
	private void printCurrentIteration(Population population, int iteration) {
		logger.debug("CURRENT ITERATION: {}", iteration);
		logger.debug("ITERATION FITNESS: {}", population.getPopulationFitness());
		for(Individual individual : population.getIndividuals())
			logger.info("{}", individual);
	}
	
	private Individual getMaxNeighborFoodSource(List<Individual> individuals) {
		long[] fitness = individuals.stream().mapToLong(Individual::getFitness).toArray();
		int minIndex = BasicOperations.getMaxValueIndex(fitness);
		return individuals.get(minIndex);
	}

}
