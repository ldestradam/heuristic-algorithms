package mx.com.lestradam.algorithms.functions.fitness;

import mx.com.lestradam.algorithms.elements.Solution;

public interface FitnessFunction {
	
	public long evaluateSolution(long[] solution);
	public long evaluateSolutionSet(Solution[] solutions);

}
