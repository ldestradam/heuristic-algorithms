package mx.com.lestradam.algorithms.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Component
public class CrossoverOperators {

	private static Logger logger = LoggerFactory.getLogger(CrossoverOperators.class);

	@Autowired
	private DataSet dataset;

	@Autowired
	private AlgorithmsParameters parameters;

	public List<long[]> orderCrossover(final long[] parent1, final long[] parent2) {
		if (logger.isTraceEnabled()) {
			logger.trace("Order Crossover operator");
			logger.trace("Parent 1: {}", Arrays.toString(parent1));
			logger.trace("Parent 2: {}", Arrays.toString(parent2));
		}
		boolean success = false;
		int crossoverPoint1 = 0;
		int crossoverPoint2 = 0;
		while (!success) {
			crossoverPoint1 = BasicOperations.getRandomlyPoint(parent1.length);
			crossoverPoint2 = BasicOperations.getRandomlyPoint(parent1.length);
			success = crossoverPoint1 != crossoverPoint2;
		}
		int start = Math.min(crossoverPoint1, crossoverPoint2);
		int end = Math.max(crossoverPoint1, crossoverPoint2);
		logger.trace("Crossover points: {} - {}", start, end);
		long[] offspring1 = orderCrossoverOperation(parent1, parent2, start, end);
		long[] offspring2 = orderCrossoverOperation(parent2, parent1, start, end);
		if (logger.isTraceEnabled()) {
			logger.trace("Offspring 1 created: Size: {} - {}", offspring1.length, Arrays.toString(offspring1));
			logger.trace("Offspring 2 created: Size: {} - {}", offspring2.length, Arrays.toString(offspring2));
		}
		return Arrays.asList(offspring1, offspring2);
	}

	private long[] orderCrossoverOperation(final long[] parent1, final long[] parent2, final int crossoverPoint1, final int crossoverPoint2) {
		Node depot = dataset.getDepot();
		List<Long> offspring = new ArrayList<>();
		for (int i = crossoverPoint1; i <= crossoverPoint2; i++) {
			if (parent1[i] != depot.getId())
				offspring.add(parent1[i]);
		}
		for (int i = crossoverPoint2 + 1; i < parent2.length; i++) {
			if (!offspring.contains(parent2[i]) && parent2[i] != depot.getId())
				offspring.add(parent2[i]);
		}
		Collections.reverse(offspring);
		for (int i = 0; i < crossoverPoint2 + 1; i++) {
			if (!offspring.contains(parent2[i]) && parent2[i] != depot.getId())
				offspring.add(parent2[i]);
		}
		Collections.reverse(offspring);
		return generateSolution(offspring);
	}

	private long[] generateSolution(final List<Long> offspring) {
		List<long[]> routes = new ArrayList<>();
		for (int i = 0; i < parameters.getNumFleet(); i++)
			routes.add(new long[] { dataset.getDepot().getId() });
		for (int i = 0; i < offspring.size(); i++) {
			int routeInd = RoutesOperations.getFeasibleRouteIndex(routes, dataset.getEdges(), dataset.getNodes(),
					offspring.get(i), parameters.getFleetCapacity());
			long[] updatedRoute = ArrayUtils.add(routes.get(routeInd), offspring.get(i));
			routes.set(routeInd, updatedRoute);
		}
		return RoutesOperations.generateSolutionFromRoutes(routes);
	}

}
