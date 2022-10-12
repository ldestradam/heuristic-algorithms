package mx.com.lestradam.algorithms.data;

public class GeneticParameters {
	
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
	
}
