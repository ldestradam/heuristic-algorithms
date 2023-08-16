package mx.com.lestradam.algorithms.elements;

import java.util.Arrays;

public class PSOSolution {
	
	private double[] position;
	private double[] velocity;
	private double[] bestPosition;
	private long[] solution;
	private double fitnessBestPosition;
	private double fitness;
	
	public PSOSolution( double[] position, double[] velocity) {
		this.position = position;
		this.velocity = velocity;
	}

	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}

	public double[] getVelocity() {
		return velocity;
	}

	public void setVelocity(double[] velocity) {
		this.velocity = velocity;
	}

	public double[] getBestPosition() {
		return bestPosition;
	}

	public void setBestPosition(double[] bestPosition) {
		this.bestPosition = bestPosition;
	}

	public double getFitnessBestPosition() {
		return fitnessBestPosition;
	}

	public void setFitnessBestPosition(double fitnessBestPosition) {
		this.fitnessBestPosition = fitnessBestPosition;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public long[] getSolution() {
		return solution;
	}

	public void setSolution(long[] solution) {
		this.solution = solution;
	}

	@Override
	public String toString() {
		return "PSOSolution [position=" + Arrays.toString(position) + ", velocity=" + Arrays.toString(velocity)
				+ ", bestPosition=" + Arrays.toString(bestPosition) + ", solution=" + Arrays.toString(solution)
				+ ", fitnessBestPosition=" + fitnessBestPosition + ", fitness=" + fitness + "]";
	}
	
	
	
}
