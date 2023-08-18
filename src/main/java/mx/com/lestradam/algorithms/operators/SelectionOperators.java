package mx.com.lestradam.algorithms.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.lestradam.algorithms.elements.Solution;

public class SelectionOperators {

	private static Logger logger = LoggerFactory.getLogger(SelectionOperators.class);

	private SelectionOperators() {
		throw new IllegalStateException("Utility class");
	}

	public static Solution rouletteSelection(Solution[] individuals, double populationFitness) {
		// Spin roulette wheel
		double rouletteWheelPosition = Math.random() * populationFitness;
		logger.trace("Selection by roulette Amount: {} Wheel position: {}", populationFitness, rouletteWheelPosition);
		// Find parent
		double spinWheel = 0;
		for (int i = 0; i < individuals.length; i++) {
			spinWheel += individuals[i].getFitness();
			if (spinWheel >= rouletteWheelPosition) {
				if (logger.isTraceEnabled())
					logger.trace("Individual selected[{}]: {}", i, individuals[i]);
				return individuals[i];
			}
		}
		if (logger.isTraceEnabled())
			logger.trace("Last individual[{}]: {}", (individuals.length - 1), individuals[individuals.length - 1]);
		return individuals[individuals.length - 1];
	}

}
