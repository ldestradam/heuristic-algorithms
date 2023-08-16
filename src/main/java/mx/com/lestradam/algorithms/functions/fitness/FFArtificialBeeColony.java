package mx.com.lestradam.algorithms.functions.fitness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.Solution;

@Component("FFArtificialBeeColony")
public class FFArtificialBeeColony {
	
	private static Logger logger = LoggerFactory.getLogger(FFArtificialBeeColony.class);
	
	@Autowired
	private ObjectiveFunction objFunction;
	
	@Autowired
	private PenaltyFunction penalty;
	
	public double evaluateSolution(long[] solution) {
		return objFunction.evaluate(solution);
	}
	
	public double excess(long[] solution) {
		return penalty.excess(solution);
	}
	
	public double[] calculateProbabilities(Solution[] foodSources) {
		// Calculate the probability for each food source
		double totalFitness = 0.0;
		double[] fitness = new double[foodSources.length];
		double[] probabilities = new double[foodSources.length];
		for (int i = 0 ; i < foodSources.length; i++) {
			fitness[i] = evaluateSolutionFitness(foodSources[i].getFitness());
			totalFitness += fitness[i];
		}
		for (int i = 0 ; i < foodSources.length; i++) {
			probabilities[i] = fitness[i] / totalFitness;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Total fitness: {}", totalFitness);
			logger.trace("Fitness: {}", fitness);
			logger.trace("Probabilities: {}", probabilities);
		}
		return probabilities;
	}
	
	public double evaluateSolutionFitness(double fitness) {
		return (fitness >= 0) ? (1.0 / (1 + fitness)) : (1.0 + Math.abs(fitness));
	}

}
