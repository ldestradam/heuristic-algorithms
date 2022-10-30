package mx.com.lestradam.algorithms.functions.builders;

public interface SolutionBuilder {
	
	/**
	* Create a solution representation from the list of client nodes, depot and number of fleets
	* @return Representation of the solution based on the given implementation
	*/
	public long[] createSolution();

}
