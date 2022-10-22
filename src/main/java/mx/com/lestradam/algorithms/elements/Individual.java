package mx.com.lestradam.algorithms.elements;

import java.util.Arrays;

public class Individual{
	
	private long[] chromosome;
	private long fitness = -1;
	
	public Individual(long[] chromosome, long fitness) {
		this.chromosome = chromosome;
		this.fitness = fitness;
	}

	public Individual(long[] chromosome) {
		this.chromosome = chromosome;
	}

	public long[] getChromosome() {
		return chromosome;
	}

	public void setChromosome(long[] chromosome) {
		this.chromosome = chromosome;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}
	
	@Override
	public String toString() {
		return "Individual [chromosome=" + Arrays.toString(chromosome) + ", fitness=" + fitness + "]";
	}

}
