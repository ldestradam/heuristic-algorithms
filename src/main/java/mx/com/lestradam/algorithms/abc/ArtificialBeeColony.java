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
	
	public Population execute() {
		// Randomly generate a set of solutions as initial food sources 
		// and evaluate their nectar (fitness)
		int foodSourceSize = params.getFoodSourceSize();
		Population foodSources = populationOps.initPopulation(foodSourceSize);		
		// Initialize count of number of iterations and 
		// count for each iteration where solution xi does not show an improvement.
		int iteration = 0;
		long[] foodSourceLimits = new long[foodSourceSize];
		while(iteration < params.getNumIterations()) {
			// Print current iteration
			if (logger.isDebugEnabled())
				printCurrentIteration(foodSources, iteration);
			// for each employed bee
			for(int i = 0; i < foodSourceSize; i++){
				long[] xi = foodSources.getIndividual(i).getChromosome();
				//Find a new food source in its neighborhood, and evaluate the fitness.
				long[] xt = neighborhood.randomSwaps(xi);
				// Apply greedy selection on the two food sources.
				long fitnessXi = populationOps.getIndividualFitness(xi);				
				long fitnessXt = populationOps.getIndividualFitness(xt);
				if(fitnessXt > fitnessXi){
					Individual fooSource = new Individual(xt, fitnessXt);					
					foodSources.setIndividual(i, fooSource);
					foodSourceLimits[i] = 0;
				} else {
					foodSourceLimits[i] = foodSourceLimits[i] + 1;
				}
			}
			//For each onlooker 
			List<List<Individual>> foodSourcesNeighborhood = new ArrayList<>();
			for(int i = 0; i < foodSourceSize; i++)
				foodSourcesNeighborhood.add(new ArrayList<>());
			for(int i = 0; i < foodSourceSize; i++){
				//Select a food source using the fitness-based roulette wheel selection method
				int index = SelectionOperators.rouletteSelectionIndex(foodSources.getIndividuals(), foodSources.getPopulationFitness());
				//Apply a neighborhood operator on xi -> xt
				long[] xi = neighborhood.randomSwaps(foodSources.getIndividual(index).getChromosome());
				long[] xt = neighborhood.randomSwaps(xi);
				long fitnessXt = populationOps.getIndividualFitness(xt);
				Individual foodSource = new Individual(xt, fitnessXt);
				foodSourcesNeighborhood.get(index).add(foodSource);
			}
			// For each food source xi and Gi != empty
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
			// If any employed bee becomes scout bee
			for(int i = 0; i < foodSourceSize; i++){
				//Send the scout bee to a randomly produced food source.
				if(foodSourceLimits[i] == params.getImprovedLimit()){
					Individual randFoodSource = populationOps.createIndividual();
					foodSources.setIndividual(i, randFoodSource);
				}
			}
			// Increase iteration counter
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
