package mx.com.lestradam.algorithms.genetic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.functions.builders.SolutionSetBuilder;
import mx.com.lestradam.algorithms.functions.fitness.FFGeneticAlgorithm;
import mx.com.lestradam.algorithms.operators.CrossoverOperators;
import mx.com.lestradam.algorithms.operators.NeighborhoodOperators;
import mx.com.lestradam.algorithms.operators.SelectionOperators;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class GeneticAlgorithm {
	
	private static Logger logger = LoggerFactory.getLogger(GeneticAlgorithm.class);
	
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
	
	public SolutionSet initial() {
		List<long[]> tempSolutions = solutionBuilder.init(params.getPopulationSize());
		Solution[] actualSolutions = new Solution[params.getPopulationSize()];
		long totalFitness = 0;
		for(int i = 0; i < params.getPopulationSize(); i++) {
			long fitness = fitnessFunc.evaluateSolution(tempSolutions.get(i));
			totalFitness += fitness;
			actualSolutions[i] = new Solution(tempSolutions.get(i), fitness);
		}
		return new SolutionSet(actualSolutions, totalFitness);
	}
	
	public SolutionSet execute() {
		int generation = 1;
		// Initialize population
		SolutionSet population = initial();
		while(generation < params.getNumGenerations()) {
			// Print current generation
			LogWriter.printCurrentIteration(population, generation);
			// Create temporary population
			SolutionSet tempPopulation = new SolutionSet(params.getPopulationSize());
			//Loop over current population
			for(int i = 0; i < params.getPopulationSize(); i = i +2) {
				// Select parents
				Solution parent1 = SelectionOperators.rouletteSelection(population.getSolutions(), population.getFitness());
				Solution parent2 = SelectionOperators.rouletteSelection(population.getSolutions(), population.getFitness());
				//Apply crossover
				if(params.getCrossoverRate() > Math.random()) {
					//Initialize offspring
					List<long[]> offsprings = crossover.orderCrossover(parent1.getRepresentation(), parent2.getRepresentation());
					long[] chromosome1 = offsprings.get(0);
					long[] chromosome2 = offsprings.get(1);
					// Apply mutation
					if(params.getMutationRate() > Math.random()) {
						chromosome1 = neighborhood.randomSwaps(chromosome1);
						chromosome2 = neighborhood.randomSwaps(chromosome2);
					}
					//Add offsprings to new population
					Solution offspring1 = new Solution(chromosome1);
					offspring1.setFitness(fitnessFunc.evaluateSolution(offspring1.getRepresentation()) );
					Solution offspring2 = new Solution(chromosome2);
					offspring2.setFitness(fitnessFunc.evaluateSolution(offspring2.getRepresentation()));
					tempPopulation.setSolution(i, offspring1);
					tempPopulation.setSolution(i + 1, offspring2);
				}else {
					// Add parents to new population without applying crossover
					tempPopulation.setSolution(i, parent1);
					tempPopulation.setSolution(i + 1, parent2);
				}
			}
			// Set fitness population
			long populationFitness = fitnessFunc.evaluateSolutionSet(tempPopulation.getSolutions());
			tempPopulation.setFitness(populationFitness);
			// Increase generation counter
			generation++;
			// Set temporary population as new population
			population = tempPopulation;
		}
		return population;
	}
	
}