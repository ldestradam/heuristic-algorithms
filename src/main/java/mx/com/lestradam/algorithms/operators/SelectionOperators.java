package mx.com.lestradam.algorithms.operators;

import mx.com.lestradam.algorithms.elements.Individual;

public class SelectionOperators {
	
	private SelectionOperators() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static Individual rouletteSelection(Individual[] individuals, double populationFitness) {
		// Spin roulette wheel
		double rouletteWheelPosition = Math.random() * populationFitness;
		//Find parent
		double spinWheel = 0;
		for(Individual individual: individuals) {
			spinWheel += individual.getFitness();
			if (spinWheel >= rouletteWheelPosition)
				return individual;
		}
		return individuals[individuals.length - 1];
	}
	
	public static int rouletteSelectionIndex(Individual[] individuals, double populationFitness) {
		// Spin roulette wheel
		double rouletteWheelPosition = Math.random() * populationFitness;
		//Find parent
		double spinWheel = 0;
		for(int i = 0; i < individuals.length; i++) {
			spinWheel += individuals[i].getFitness();
			if (spinWheel >= rouletteWheelPosition)
				return i;
		}
		return individuals.length - 1;
	}

}
