package mx.com.lestradam.algorithms.elements;

import java.util.Arrays;

public class Solution {

	private long[] representation;
	private double fitness;
	private long overcap;

	public Solution(long[] representation) {
		this.representation = representation;
		this.fitness = -1;
		this.overcap = 0;
	}

	public Solution(long[] representation, double fitness, long overcap) {
		this.representation = representation;
		this.fitness = fitness;
		this.overcap = overcap;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public long[] getRepresentation() {
		return representation;
	}

	public void setRepresentation(long[] representation) {
		this.representation = representation;
	}

	public long getOvercap() {
		return overcap;
	}

	public void setOvercap(long overcap) {
		this.overcap = overcap;
	}

	@Override
	public String toString() {
		return "Solution [fitness=" + fitness + ", overcap=" + overcap + ", size=" + representation.length
				+ ", representation=" + Arrays.toString(representation) + "]";
	}

}
