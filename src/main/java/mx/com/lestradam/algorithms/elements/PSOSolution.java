package mx.com.lestradam.algorithms.elements;

public class PSOSolution {
	
	private double[] position;
	private double[] velocity;
	private double[] bestPosition;
	private long[] solution;
	private long fitnessBestPosition;
	private long fitness;
	
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

	public long getFitnessBestPosition() {
		return fitnessBestPosition;
	}

	public void setFitnessBestPosition(long fitnessBestPosition) {
		this.fitnessBestPosition = fitnessBestPosition;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}

	public long[] getSolution() {
		return solution;
	}

	public void setSolution(long[] solution) {
		this.solution = solution;
	}
	
}
