package mx.com.lestradam.algorithms.elements;

public class GeneticParameters {
	
	private int numGenerations;
	private int populationSize;
	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;
	
	public double getMutationRate() {
		return mutationRate;
	}
	
	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	public double getCrossoverRate() {
		return crossoverRate;
	}
	
	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}
	
	public int getElitismCount() {
		return elitismCount;
	}
	
	public void setElitismCount(int elitismCount) {
		this.elitismCount = elitismCount;
	}

	public int getNumGenerations() {
		return numGenerations;
	}

	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	
}
