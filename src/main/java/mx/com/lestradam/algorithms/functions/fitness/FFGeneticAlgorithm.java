package mx.com.lestradam.algorithms.functions.fitness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.Solution;

@Component("FFGeneticAlgorithm")
public class FFGeneticAlgorithm {
	
	@Autowired
	private ObjectiveFunction objFunction;

	public long evaluateSolution(long[] solution){
		return objFunction.evaluate(solution);
	}

	public long evaluateSolutionSet(Solution[] solutions) {
		long totalFitness = 0;
		for(Solution individual: solutions)
			totalFitness += evaluateSolution(individual.getRepresentation());
		return totalFitness;
	}

}
