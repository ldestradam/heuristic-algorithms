package mx.com.lestradam.algorithms.operators;

import java.util.Arrays;

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

	public static Solution inverseRouletteSelection(Solution[] individuals) {
		// Calculate Selection Probabilities and the Sum of Selection Probabilities
		double totalFitness = 0;
		double[] probabilities = new double[individuals.length];
		for (int i = 0; i < individuals.length; i++) {
			probabilities[i] = (1 / individuals[i].getFitness());
			totalFitness += probabilities[i];
		}
		// Normalize Probabilities
		for (int i = 0; i < individuals.length; i++) {
			probabilities[i] = probabilities[i] / totalFitness;
		}
		// Spin roulette wheel
		double rouletteWheelPosition = Math.random();
		if (logger.isTraceEnabled()) {
			logger.trace("Selection by roulette Amount: {} Wheel position: {}", totalFitness, rouletteWheelPosition);
			logger.trace("Probabilities: {}", Arrays.toString(probabilities));
		}
		// Find parent
		double spinWheel = 0;
		for (int i = 0; i < individuals.length; i++) {
			spinWheel += probabilities[i];
			if (spinWheel >= rouletteWheelPosition) {
				if (logger.isTraceEnabled())
					logger.trace("Individual selected[{}]: Probability: {} - {}", i, probabilities[i], individuals[i]);
				return individuals[i];
			}
		}
		if (logger.isTraceEnabled())
			logger.trace("Last individual[{}]: {}", (individuals.length - 1), individuals[individuals.length - 1]);
		return individuals[individuals.length - 1];
	}

}
