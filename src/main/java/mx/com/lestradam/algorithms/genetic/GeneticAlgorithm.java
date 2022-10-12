package mx.com.lestradam.algorithms.genetic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.AlgorithmsParameters;
import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.functions.CrossoverOperations;
import mx.com.lestradam.algorithms.functions.IndividualCreation;
import mx.com.lestradam.algorithms.functions.IndividualFitness;
import mx.com.lestradam.algorithms.functions.NeighborhoodOperators;

@Component
public class GeneticAlgorithm {
	
	private static Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);
	
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
	private GeneticParameters geneticParams;
	
	@Autowired
	private AlgorithmsParameters parameters;
	
	private Population initPopulation() {
		int populationSize = parameters.getPopulationSize();
		Population population = new Population(populationSize);
		long populationFitness = 0;
		for(int i = 0; i < populationSize; i++) {
			Individual individual = this.indCreation.createIndividual();
			long indFitness = fitness.evaluate(individual.getChromosome());
			populationFitness += indFitness;
			individual.setFitness(indFitness);
			population.setIndividual(i, individual);
		}
		population.setPopulationFitness(populationFitness);
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
	
	private long getFitnessPopulation(Individual[] individuals) {
		long totalFitness = 0;
		for(Individual individual: individuals)
			totalFitness += fitness.evaluate(individual.getChromosome());
		return totalFitness;
	}
	
	public Population execute() {
		int generation = 1;
		// Initialize population
		Population population = this.initPopulation();
		while(generation < parameters.getNumGenerations()) {
			// Print current generation
			if (logger.isDebugEnabled()) 
				printCurrentGeneration(population, generation);
			// Apply crossover
			// Create temporary population
			Population tempPopulation = new Population(parameters.getPopulationSize());
			//Loop over current population
			for(int i = 0; i < parameters.getPopulationSize(); i = i +2) {
				// Select parents
				Individual parent1 = this.selectParent(population.getIndividuals(), population.getPopulationFitness());
				Individual parent2 = this.selectParent(population.getIndividuals(), population.getPopulationFitness());
				//Apply crossover
				if(geneticParams.getCrossoverRate() > Math.random()) {
					//Initialize offspring
					List<long[]> offsprings = crossover.orderCrossover(parent1.getChromosome(), parent2.getChromosome());
					long[] chromosome1 = offsprings.get(0);
					long[] chromosome2 = offsprings.get(1);
					// Apply mutation
					if(geneticParams.getMutationRate() > Math.random()) {
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
			// Set fitness population
			long populationFitness = getFitnessPopulation(tempPopulation.getIndividuals());
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