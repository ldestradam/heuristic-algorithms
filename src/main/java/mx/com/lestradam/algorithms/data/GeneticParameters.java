package mx.com.lestradam.algorithms.data;

public class GeneticParameters {
	
	private int populationSize;
	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;
	private int numFleet;
	private long fleetCapacity;
	private int numGenerations;
	
	public int getPopulationSize() {
		return populationSize;
	}
	
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	
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

	public int getNumFleet() {
		return numFleet;
	}

	public void setNumFleet(int numFleet) {
		this.numFleet = numFleet;
	}

	public long getFleetCapacity() {
		return fleetCapacity;
	}

	public void setFleetCapacity(long fleetCapacity) {
		this.fleetCapacity = fleetCapacity;
	}

	public int getNumGenerations() {
		return numGenerations;
	}

	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}
	
}
