package mx.com.lestradam.algorithms.elements;

import java.util.Arrays;

public class Solution{
	
	private long[] representation;
	private long fitness = -1;
	
	public Solution(long[] representation, long fitness) {
		this.representation = representation;
		this.fitness = fitness;
	}

	public Solution(long[] representation) {
		this.representation = representation;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}
	
	public long[] getRepresentation() {
		return representation;
	}

	public void setRepresentation(long[] representation) {
		this.representation = representation;
	}

	@Override
	public String toString() {
		return "Individual [representation=" + Arrays.toString(representation) + ", fitness=" + fitness + "]";
	}

}
