package mx.com.lestradam.algorithms.genetic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.functions.generation.PopulationOperations;
import mx.com.lestradam.algorithms.operators.CrossoverOperators;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.operators.SelectionOperators;

@Component
public class GeneticAlgorithm {
	
	private static Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);
	
	@Autowired
	private CrossoverOperators crossover;
	
	@Autowired
	private NeighborhoodOperators neighborhood;
	
	@Autowired
	private GeneticParameters params;
	
	@Autowired
	private PopulationOperations populationOps;
	
	public Population execute() {
		int generation = 1;
		// Initialize population
		Population population = populationOps.initPopulation(params.getPopulationSize());
		while(generation < params.getNumGenerations()) {
			// Print current generation
			if (logger.isDebugEnabled()) 
				printCurrentGeneration(population, generation);
			// Create temporary population
			Population tempPopulation = new Population(params.getPopulationSize());
			//Loop over current population
			for(int i = 0; i < params.getPopulationSize(); i = i +2) {
				// Select parents
				Individual parent1 = SelectionOperators.rouletteSelection(population.getIndividuals(), population.getPopulationFitness());
				Individual parent2 = SelectionOperators.rouletteSelection(population.getIndividuals(), population.getPopulationFitness());
				//Apply crossover
				if(params.getCrossoverRate() > Math.random()) {
					//Initialize offspring
					List<long[]> offsprings = crossover.orderCrossover(parent1.getChromosome(), parent2.getChromosome());
					long[] chromosome1 = offsprings.get(0);
					long[] chromosome2 = offsprings.get(1);
					// Apply mutation
					if(params.getMutationRate() > Math.random()) {
						chromosome1 = neighborhood.randomSwaps(chromosome1);
						chromosome2 = neighborhood.randomSwaps(chromosome2);
					}
					//Add offsprings to new population
					Individual offspring1 = new Individual(chromosome1);
					offspring1.setFitness(populationOps.getIndividualFitness(offspring1.getChromosome()) );
					Individual offspring2 = new Individual(chromosome2);
					offspring2.setFitness(populationOps.getIndividualFitness(offspring2.getChromosome()));
					tempPopulation.setIndividual(i, offspring1);
					tempPopulation.setIndividual(i + 1, offspring2);
				}else {
					// Add parents to new population without applying crossover
					tempPopulation.setIndividual(i, parent1);
					tempPopulation.setIndividual(i + 1, parent2);
				}
			}
			// Set fitness population
			long populationFitness = populationOps.getPopulationFitness(tempPopulation.getIndividuals());
			tempPopulation.setPopulationFitness(populationFitness);
			// Increase generation counter
			generation++;
			// Set temporary population as new population
			population = tempPopulation;
		}
		return population;
	}
	
	private void printCurrentGeneration(Population population, int generation) {
		logger.debug("CURRENT GENERATION: {}", generation);
		logger.debug("GENERATION FITNESS: {}", population.getPopulationFitness());
		for(Individual individual : population.getIndividuals())
			logger.info("{}", individual);
	}
	
}