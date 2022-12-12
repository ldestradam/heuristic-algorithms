package mx.com.lestradam.algorithms.functions.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.BasicOperation;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.PSOSolution;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

/**
 * Solution with cost minimization
 * @author leonardo estrada
 *
 */
@Component
public class SBParticleSwarmOptimization{
	
private static Logger logger = LoggerFactory.getLogger(SBParticleSwarmOptimization.class);
	
	@Autowired
	private DataSet dataset;
	
	@Autowired
	private AlgorithmsParameters parameters;
	
	public double[] generateRandomArrayNumbers() {
		int size = dataset.getNodes().size() - 1;
		double[] randomNumbers = new double[size];
		for (int i = 0; i < size; i++)
			randomNumbers[i] = Math.random();
		if (logger.isTraceEnabled())
			logger.trace("Random array generated: {}", Arrays.toString(randomNumbers));
		return randomNumbers;
	}
	
	public long[] encodePosition(double[] position) {
		List<int[]> routes = assignClientsToRoutes(position);
		return routingVehicle(routes);
	}
	
	public List<int[]> assignClientsToRoutes(double[] solution) {
		List<int[]> assignedClients = new ArrayList<>();
		List<Double> unassignedClients = Arrays.stream(solution)
				.boxed().collect(Collectors.toList());
		int maxIndex = BasicOperations.getMaxValueIndex(solution);
		int minIndex = BasicOperations.getMinValueIndex(solution);
		double maxValue = solution[maxIndex];
		double minValue = solution[minIndex];
		int numFleets = parameters.getNumFleet();
		double splitSize = (maxValue - minValue) / numFleets;
		if (logger.isTraceEnabled()) {
			logger.trace("Assigning clients...");
			logger.trace("Position: {}", Arrays.toString(solution));
			logger.trace("Min: [{}] \t Max: [{}] \t Split: [{}]", minValue, maxValue, splitSize);
		}
		for (int i = 0; i < numFleets; i++) {
			List<Integer> subSection = new ArrayList<>();
			double min = minValue + (splitSize * i);
			double max = minValue + (splitSize * (i + 1));
			if (max < maxValue && i == numFleets -1)
				max = maxValue;
			if (logger.isTraceEnabled())
				logger.trace("Min interval: [{}] \t Max interval: [{}]", min, max);
			for (int j = 0; j < unassignedClients.size(); j++) {
				double clientValue = unassignedClients.get(j);
				if (clientValue >= min && clientValue <= max) {
					subSection.add( (int) dataset.getNodes().get(j).getId());
				}
			}
			int[] routeN = subSection.stream().mapToInt(ind->ind).toArray();
			if (logger.isTraceEnabled())
				logger.trace("Route: {}", Arrays.toString(routeN));
			assignedClients.add(routeN);
		}
		return assignedClients;
	}
	
	public long[] routingVehicle(List<int[]> routes) {
		List<long[]> routesNSS = new ArrayList<>();
		for (int[] route : routes) {
			long[] routeNSS = nearestNeighborSearch(route);
			routesNSS.add(routeNSS);
		}
		long[] routing = {};
		for (long[] routeAux : routesNSS)
			routing = ArrayUtils.addAll(routing, routeAux);
		if (logger.isTraceEnabled()) 
			logger.trace("Routing: {}", Arrays.toString(routing));
		return routing;
	} 
	
	public long[] nearestNeighborSearch(int[] clients) {
		if (logger.isTraceEnabled()) {
			logger.trace("Routing clients...");
			logger.trace("Clients: {}", Arrays.toString(clients));
		}
		Node depot = dataset.getDepot();
		if (clients.length == 0)
			return new long[] {depot.getId()};
		List<Integer> assignedClients = new ArrayList<>();		
		long currentClient = depot.getId();
		int[] copiedClients = Arrays.copyOf(clients, clients.length);
		while (copiedClients.length > 0) {
			List<Long> costs = new ArrayList<>();
			for (int i = 0; i < copiedClients.length; i++) {
				long targetClient = copiedClients[i];
				long currentDistance = RoutesOperations.getDistanceNodes(currentClient, targetClient, dataset.getEdges());
				costs.add(currentDistance);
			}
			int client = BasicOperations.getMinValueIndex(costs.stream().mapToLong(ind->ind).toArray());
			assignedClients.add(copiedClients[client]);
			currentClient = copiedClients[client];
			if (logger.isTraceEnabled()) {
				logger.trace("Costs: {}", costs.stream().mapToLong(Long::longValue).toArray());	
				logger.trace("Unassigned Clients: {}", Arrays.toString(copiedClients));
				logger.trace("Min cost: {}", client);
			}
			copiedClients = ArrayUtils.remove(copiedClients, client);
		}
		assignedClients.add(0, (int) depot.getId());
		long[] routedClients = assignedClients.stream().mapToLong(ind->ind).toArray();
		if (logger.isTraceEnabled())
			logger.trace("Routed clients: {}", Arrays.toString(routedClients));
		return routedClients;
	}

}
