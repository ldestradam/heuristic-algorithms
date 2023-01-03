package mx.com.lestradam.algorithms.functions.fitness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.Solution;

@Component("FFArtificialBeeColony")
public class FFArtificialBeeColony implements FitnessFunction{
	
	@Autowired
	private ObjectiveFunction objFunction;

	@Override
	public long evaluateSolution(long[] solution) {
		long result = objFunction.evaluate(solution);
		return result >= 0 ? 1 / (1 + result) : 1 + Math.abs(result);
	}

	@Override
	public long evaluateSolutionSet(Solution[] solutions) {
		long totalFitness = 0;
		for(Solution individual: solutions)
			totalFitness += evaluateSolution(individual.getRepresentation());
		return totalFitness;
	}

}
