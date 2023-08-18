package mx.com.lestradam.algorithms.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	private long[] orderCrossoverOperation(long[] parent1, long[] parent2, int crossoverPoint1, int crossoverPoint2) {
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
		offspring = offspringOrderCrossover(offspring);
		return offspring.stream().mapToLong(i -> i).toArray();
	}

	private List<Long> offspringOrderCrossover(List<Long> offspring) {
		long totalDemand = 0;
		int currentFleets = 1;
		List<Long> aux = new ArrayList<>();
		aux.add(dataset.getDepot().getId());
		for (int i = 0; i < offspring.size(); i++) {
			long clientId = offspring.get(i);
			long demand = RoutesOperations.getClientDemand(clientId, dataset.getNodes());
			if (logger.isTraceEnabled()) {
				logger.trace("Id cliente: {} - Quantity: {}", clientId, demand);
				logger.trace("Current demand: {} - Expected demand: {}", totalDemand, (totalDemand + demand));
				logger.trace("Current solution: {}", Arrays.toString(aux.toArray()));
			}
			if (totalDemand + demand > parameters.getFleetCapacity()) {
				currentFleets++;
				if (currentFleets > parameters.getNumFleet()) {
					aux.addAll(offspring.subList(i, offspring.size()));
					if (logger.isTraceEnabled())
						logger.trace("Fleet of vehicles exceeded: {}", offspring.stream().mapToLong(c -> c).toArray());
					break;
				}
				aux.add(dataset.getDepot().getId());
				aux.add(offspring.get(i));
				totalDemand = demand;
			} else {
				aux.add(offspring.get(i));
				totalDemand += demand;
			}
		}
		return aux;
	}

	public List<long[]> orderCrossover(long[] parent1, long[] parent2) {
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
			logger.trace("Offspring 1 created: {}", Arrays.toString(offspring1));
			logger.trace("Offspring 2 created: {}", Arrays.toString(offspring2));
		}
		return Arrays.asList(offspring1, offspring2);
	}

}
