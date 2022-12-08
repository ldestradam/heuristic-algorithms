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
	
	public long[] encodePosition(double[] position) {
		List<int[]> routes = assignClientsToRoutes(position);
		return routingVehicle(routes);
	}
	
	public double[] generateRandomArrayNumbers() {
		int size = parameters.getNumFleet();
		double[] randomNumbers = new double[size];
		for(int i = 0; i < size; i++)
			randomNumbers[i] = Math.random();
		return randomNumbers;
	}
	
	public long[] routingVehicle(List<int[]> routes) {
		List<long[]> routesNSS = new ArrayList<>();
		for(int[] route : routes) {
			long[] routeNSS = nearestNeighborSearch(route);
			routesNSS.add(routeNSS);
		}
		long[] routing = {};
		for(long[] routeAux : routesNSS)
			routing = ArrayUtils.addAll(routing, routeAux);
		return routing;
	}
	
	public List<int[]> assignClientsToRoutes(double[] continuousSolution) {
		List<int[]> assignedClients = new ArrayList<>();
		List<Double> unassignedClients = Arrays.stream(continuousSolution)
				.boxed().collect(Collectors.toList());
		int maxIndex = BasicOperations.getMaxValueIndex(continuousSolution);
		int minIndex = BasicOperations.getMinValueIndex(continuousSolution);
		double maxValue = continuousSolution[maxIndex];
		double minValue = continuousSolution[minIndex];
		int numFleets = parameters.getNumFleet();
		double splitSize = (maxValue - minValue) / numFleets;
		for(int i = 0; i < numFleets - 1; i++) {
			List<Integer> subSection = new ArrayList<>();
			double min = minValue + (splitSize * i);
			double max = minValue + (splitSize * (i + 1));
			for(int j = 0; j < unassignedClients.size(); j++) {
				double clientValue = unassignedClients.get(j);
				if(clientValue >= min && clientValue < max) {
					subSection.add(j);
				}
			}
			int[] routeN = subSection.stream().mapToInt(ind->ind).toArray();
			assignedClients.add(routeN);
		}
		return assignedClients;
	} 
	
	public long[] nearestNeighborSearch(int[] clients) {
		List<Integer> assignedClients = new ArrayList<>();
		Node depot = dataset.getDepot();
		long currentClient = depot.getId();
		while(assignedClients.size() <= clients.length) {
			List<Long> costs = new ArrayList<>();
			for(int i = 0; i < clients.length; i++) {
				long targetClient = clients[i];
				long currentDistance = RoutesOperations.getDistanceNodes(currentClient, targetClient, dataset.getEdges());
				costs.add(currentDistance);
			}
			int client = BasicOperations.getMinValueIndex(costs.stream().mapToLong(ind->ind).toArray());
			assignedClients.add(clients[client]);
			currentClient = clients[client];
			clients = ArrayUtils.remove(clients, client);
		}
		assignedClients.add(0, (int) depot.getId());
		return assignedClients.stream().mapToLong(ind->ind).toArray();
	}

}
