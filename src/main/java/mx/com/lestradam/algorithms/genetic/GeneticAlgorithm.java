package mx.com.lestradam.algorithms.genetic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.functions.CrossoverOperations;
import mx.com.lestradam.algorithms.functions.IndividualCreation;
import mx.com.lestradam.algorithms.functions.IndividualFitness;
import mx.com.lestradam.algorithms.functions.NeighborhoodOperators;

@Component
public class GeneticAlgorithm {
	
	@Autowired
	@Qualifier("IndividualsWithCostMinimization")
	private IndividualCreation indCreation;
	
	@Autowired
	@Qualifier("SimpleFitness")
	private IndividualFitness fitness;
	
	@Autowired
	private CrossoverOperations crossover;
	
	@Autowired
	private NeighborhoodOperators neighborhood;
	
	@Autowired
	private GeneticParameters parameters;
	
	private Population initPopulation() {
		int populationSize = parameters.getPopulationSize();
		Population population = new Population(populationSize);
		for(int i = 0; i < populationSize; i++) {
			Individual individual = this.indCreation.createIndividual();
			individual.setFitness(fitness.evaluate(individual.getChromosome()));
			population.setIndividual(i, individual);
		}
		return population;
	}
	
	private Individual selectParent(Individual[] individuals, double populationFitness) {
		// Spin roulette wheel
		double rouletteWheelPosition = Math.random() * populationFitness;
		//Find parent
		double spinWheel = 0;
		for(Individual individual: individuals) {
			spinWheel += individual.getFitness();
			if (spinWheel >= rouletteWheelPosition)
				return individual;
		}
		return individuals[individuals.length - 1];
	}
	
	public Population execute() {
		int generation = 1;
		// Initialize population
		Population population = this.initPopulation();
		while(generation < parameters.getNumGenerations()) {
			// Apply crossover
			//Create temporary population
			Population tempPopulation = new Population(parameters.getPopulationSize());
			//Loop over current population
			for(int i = 0; i < parameters.getPopulationSize(); i = i +2) {
				// Select parents
				Individual parent1 = this.selectParent(population.getIndividuals(), population.getPopulationFitness());
				Individual parent2 = this.selectParent(population.getIndividuals(), population.getPopulationFitness());
				//Apply crossover
				if(parameters.getCrossoverRate() > Math.random()) {
					//Initialize offspring
					List<long[]> offsprings = crossover.orderCrossover(parent1.getChromosome(), parent2.getChromosome());
					long[] chromosome1 = offsprings.get(0);
					long[] chromosome2 = offsprings.get(1);
					// Apply mutation
					if(parameters.getMutationRate() > Math.random()) {
						chromosome1 = neighborhood.randomSwaps(chromosome1);
						chromosome2 = neighborhood.randomSwaps(chromosome2);
					}
					//Add offsprings to new population
					Individual offspring1 = new Individual(chromosome1);
					offspring1.setFitness(fitness.evaluate(offspring1.getChromosome()));
					Individual offspring2 = new Individual(chromosome2);
					offspring2.setFitness(fitness.evaluate(offspring2.getChromosome()));
					tempPopulation.setIndividual(i, offspring1);
					tempPopulation.setIndividual(i + 1, offspring2);
				}else {
					// Add parents to new population without applying crossover
					tempPopulation.setIndividual(i, parent1);
					tempPopulation.setIndividual(i + 1, parent2);
				}
			}
			// Increase generation counter
			generation++;
			// Set temporary population as new population
			population = tempPopulation;
		}
		return population;
	}
	
}