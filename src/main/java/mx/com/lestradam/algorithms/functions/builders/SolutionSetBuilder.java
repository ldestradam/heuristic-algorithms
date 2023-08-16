package mx.com.lestradam.algorithms.functions.builders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Interface for the creation of solutions
 * To facilitate the exchange of implementation of creation of solutions.
 * 
 * @author leonardo estrada
 *
 */
@Component
public class SolutionSetBuilder {
	
	@Autowired
	@Qualifier("RandomizedGreedy")
	private SolutionBuilder solutionBuilder;
	
	/**
	 * Create a set of solutions from the list of client nodes, depot and number of fleets
	 * @param setSize Set size
	 * @return Representation of the solution based on the given implementation
	 */
	public List<long[]> init(int setSize) {
		List<long[]> solutions = new ArrayList<>();
		for(int i = 0; i < setSize; i++) {
			long[] solution = createSolution();
			solutions.add(solution);
		}
		return solutions;
	}
	
	/**
     * Create a solution from the list of client nodes, depot and number of fleets
     * @return Representation of the solution based on the given implementation
     */
	public long[] createSolution() {
		return solutionBuilder.createSolution();
	}

}
