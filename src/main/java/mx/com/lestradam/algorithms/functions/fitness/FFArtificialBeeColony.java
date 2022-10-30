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
		return objFunction.evaluate(solution);
	}

	@Override
	public long evaluateSolutionSet(Solution[] solutions) {
		long totalFitness = 0;
		for(Solution individual: solutions)
			totalFitness += evaluateSolution(individual.getRepresentation());
		return totalFitness;
	}

}
