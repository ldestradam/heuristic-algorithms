package mx.com.lestradam.algorithms.functions.generation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.functions.fitness.IndividualFitness;

@Component
public class PopulationOperations {
	
	@Autowired
	@Qualifier("IndividualsWithCostMinimization")
	private IndividualCreation indCreation;
	
	@Autowired
	@Qualifier("SimpleFitness")
	private IndividualFitness fitness;
	
	public Population initPopulation(int populationSize) {
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
	
	public Individual createIndividual() {
		Individual individual = this.indCreation.createIndividual();
		long indFitness = getIndividualFitness(individual.getChromosome());
		individual.setFitness(indFitness);
		return individual; 
	}
	
	public long getIndividualFitness(long[] chromosome) {		
		return fitness.evaluate(chromosome);
	}
	
	public long getPopulationFitness(Individual[] individuals) {
		long totalFitness = 0;
		for(Individual individual: individuals)
			totalFitness += fitness.evaluate(individual.getChromosome());
		return totalFitness;
	}

}
