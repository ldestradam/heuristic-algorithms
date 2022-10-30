package mx.com.lestradam.algorithms.elements;

public class SolutionSet {
	
	private Solution[] solutions;
	private long fitness = -1;
	
	public SolutionSet(int populationSize) {
		this.solutions = new Solution[populationSize];
	}
	
	public SolutionSet(Solution[] solutions, long fitness) {
		this.solutions = solutions;
		this.fitness = fitness;
	}
	
	public void setSolution(int offset, Solution solution) {
		solutions[offset] = solution;
	}
	
	public Solution getSolution(int offset) {
		return solutions[offset];
	}

	public Solution[] getSolutions() {
		return solutions;
	}

	public void setSolutions(Solution[] solutions) {
		this.solutions = solutions;
	}

	public long getFitness() {
		return fitness;
	}

	public void setFitness(long fitness) {
		this.fitness = fitness;
	}

}
