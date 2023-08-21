package mx.com.lestradam.algorithms.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
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
		logger.debug("Creating initial population...");
		List<long[]> tempSolutions = solutionBuilder.init(params.getPopulationSize());
		Solution[] actualSolutions = new Solution[params.getPopulationSize()];
		long totalFitness = 0;
		for (int i = 0; i < params.getPopulationSize(); i++) {
			double fitness = fitnessFunc.evaluateSolution(tempSolutions.get(i));
			long excess = fitnessFunc.excess(tempSolutions.get(i));
			actualSolutions[i] = new Solution(tempSolutions.get(i), fitness, excess);
			totalFitness += fitness;
			if (logger.isDebugEnabled())
				logger.debug("Individual[{}] created: {}", i, actualSolutions[i]);
		}
		return new SolutionSet(actualSolutions, totalFitness);
	}

	public SolutionSet execute() {
		int generation = 1;
		// Initialize population
		SolutionSet population = initial();
		LogWriter.printCurrentIteration(population, generation);
		while (generation < params.getNumGenerations()) {
			// Create temporary population
			List<Solution> tempPopulation = new ArrayList<>();
			// Loop over current population
			while (tempPopulation.size() < (params.getPopulationSize() - params.getElitism())) {
				// Select parents
				logger.debug("Parent selection");
				Solution parent1 = SelectionOperators.inverseRouletteSelection(population.getSolutions());
				Solution parent2 = SelectionOperators.inverseRouletteSelection(population.getSolutions());
				if (logger.isDebugEnabled()) {
					logger.debug("Parent 1: {}", Arrays.toString(parent1.getRepresentation()));
					logger.debug("Parent 2: {}", Arrays.toString(parent2.getRepresentation()));
				}
				// Apply crossover
				if (params.getCrossoverRate() > Math.random()) {
					// Initialize offspring
					logger.debug("Crossover operation");
					List<long[]> offsprings = crossover.orderCrossover(parent1.getRepresentation(),
							parent2.getRepresentation());
					long[] chromosome1 = offsprings.get(0);
					long[] chromosome2 = offsprings.get(1);
					if (logger.isDebugEnabled()) {
						logger.debug("Offspring 1: {}", Arrays.toString(chromosome1));
						logger.debug("Offspring 2: {}", Arrays.toString(chromosome2));
					}
					// Apply mutation
					logger.debug("Mutation operation");
					if (params.getMutationRate() > Math.random()) {
						chromosome1 = neighborhood.randomSwaps(chromosome1);
						chromosome2 = neighborhood.randomSwaps(chromosome2);
						if (logger.isDebugEnabled()) {
							logger.debug("Offspring mutated 1: {}", Arrays.toString(chromosome1));
							logger.debug("Offspring mutated 2: {}", Arrays.toString(chromosome2));
						}
					}
					// Add offsprings to new population
					Solution offspring1 = new Solution(chromosome1);
					offspring1.setFitness(fitnessFunc.evaluateSolution(chromosome1));
					offspring1.setOvercap(fitnessFunc.excess(chromosome1));
					Solution offspring2 = new Solution(chromosome2);
					offspring2.setFitness(fitnessFunc.evaluateSolution(chromosome2));
					offspring2.setOvercap(fitnessFunc.excess(chromosome2));
					tempPopulation.add(offspring1);
					if (tempPopulation.size() != (params.getPopulationSize() - params.getElitism()))
						tempPopulation.add(offspring2);
				} else {
					// Add parents to new population without applying crossover
					logger.debug("Crossover operation not applied");
					tempPopulation.add(parent1);
					if (tempPopulation.size() != (params.getPopulationSize() - params.getElitism()))
						tempPopulation.add(parent2);
				}
			}
			// Elitism: Keep the top N best-performing individuals from the current
			// generation
			logger.debug("Elitism: {}", params.getElitism());
			double[] fitnesses = Arrays.stream(population.getSolutions()).mapToDouble(Solution::getFitness).toArray();
			Solution[] newPopulation = new Solution[params.getPopulationSize()];
			for (int i = 0; i < params.getElitism(); i++) {
				int ind = BasicOperations.getNthMinValueIndex(fitnesses, i);
				newPopulation[i] = population.getSolution(ind);
				if (logger.isDebugEnabled()) {
					logger.debug("Best solution[{}] {}", i, newPopulation[i]);
				}
			}
			// Fill the rest of the new population with the remaining offspring
			for (int i = 0; i < tempPopulation.size(); i++) {
				newPopulation[params.getElitism() + i] = tempPopulation.get(i);
			}
			// Set fitness population
			long populationFitness = fitnessFunc.evaluateSolutionSet(newPopulation);
			// Increase generation counter
			generation++;
			// Set temporary population as new population
			logger.debug("Setting temporary population as new population");
			population.setSolutions(newPopulation);
			population.setFitness(populationFitness);
			// Print current generation
			LogWriter.printCurrentIteration(population, generation);
		}
		return population;
	}

}