package mx.com.lestradam.algorithms.elements;

public class Population {
	
	private Individual[] individuals;
	private long populationFitness = -1;
	
	public Population(int populationSize) {
		this.individuals = new Individual[populationSize];
	}
	
	public Individual[] getIndividuals() {
		return this.individuals;
	}
	
	public void setIndividual(int offset, Individual individual) {
		individuals[offset] = individual;
	}
	
	public Individual getIndividual(int offset) {
		return individuals[offset];
	}

	public long getPopulationFitness() {
		return populationFitness;
	}

	public void setPopulationFitness(long populationFitness) {
		this.populationFitness = populationFitness;
	}

}
