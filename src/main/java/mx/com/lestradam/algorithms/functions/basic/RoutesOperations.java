package mx.com.lestradam.algorithms.functions.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.lestradam.algorithms.elements.Edge;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.exceptions.DataException;

public class RoutesOperations {
	
	private static Logger logger = LoggerFactory.getLogger(RoutesOperations.class);

	private RoutesOperations() {
		throw new IllegalStateException("Utility class");
	}

	public static long getDistanceNodes(long source, long target, List<Edge> edges) {
		if (source == target)
			return 0;
		Optional<Edge> edgeA = edges.stream().filter(
				currentegde -> (int) currentegde.getSource() == source && (int) currentegde.getTarget() == target)
				.findFirst();
		Optional<Edge> edgeB = edges.stream().filter(
				currentegde -> (int) currentegde.getSource() == target && (int) currentegde.getTarget() == source)
				.findFirst();
		if (!edgeA.isPresent() && !edgeB.isPresent())
			throw new DataException(
					"Edge not found with source: " + source + " and target: " + target + " and vicecersa");
		return edgeA.isPresent() ? edgeA.get().getWeight() : edgeB.get().getWeight();
	}

	public static long getDistanceRoute(long[] route, List<Edge> edges) {
		long sum = 0;
		for (int i = 0; i < route.length; i++) {
			long source = route[i];
			long target = (i == route.length - 1) ? route[0] : route[i + 1];
			sum += getDistanceNodes(source, target, edges);
		}
		return sum;
	}

	public static List<long[]> splitIntoRoute(long[] solution, long depot) {
		List<long[]> routes = new ArrayList<>();
		int offset = 0;
		int depotInd = getNextDepot(solution, offset, depot);
		do {
			routes.add(Arrays.copyOfRange(solution, offset, depotInd));
			offset = depotInd;
			depotInd = getNextDepot(solution, offset, depot);
			if (depotInd == 0)
				routes.add(Arrays.copyOfRange(solution, offset, solution.length));
		} while (depotInd != 0);
		return routes;
	}

	public static int getNextDepot(long[] solution, int offset, long depot) {
		for (int i = offset + 1; i < solution.length; i++) {
			if (solution[i] == depot)
				return i;
		}
		return 0;
	}

	public static long getClientDemand(long client, List<Node> clients) {
		Node actualClient = clients.stream().filter(c -> c.getId() == client).findFirst()
				.orElseThrow(() -> new DataException("Client with id " + client + " not found"));
		return actualClient.getQuantity();
	}

	public static long getRouteOverCap(long[] route, List<Node> clients, long capacity) {
		long actualCapacity = 0;
		for (int i = 0; i < route.length; i++) {
			actualCapacity += getClientDemand(route[i], clients);
		}
		return (actualCapacity > capacity) ? actualCapacity - capacity : 0;
	}

	public static long getSolutionOverCap(long[] solution, List<Node> clients, long capacity, long depot) {
		long overCap = 0;
		List<long[]> routes = splitIntoRoute(solution, depot);
		for (long[] route : routes) {
			overCap += getRouteOverCap(route, clients, capacity);
		}
		return overCap;
	}

	public static long[] generateSolutionFromRoutes(final List<long[]> routes) {
		long[] solution = {};
		for (long[] routeAux : routes)
			solution = ArrayUtils.addAll(solution, routeAux);
		return solution;
	}
	
	public static int getFeasibleRouteIndex(final List<long[]> routes, final List<Edge> edges, List<Node> nodes, final long customer, final long capacity) {
		logger.trace("Checking feasible route for customer: {}", customer);
		long[] distances = new long[routes.size()];
		// Evaluate the cost of adding the selected customer to each of the available
		// vehicle routes.
		for (int i = 0; i < routes.size(); i++) {
			long[] expectedRoute = ArrayUtils.add(routes.get(i), customer);
			distances[i] = getDistanceRoute(expectedRoute, edges);
		}
		if (logger.isTraceEnabled())
			logger.trace("Expected cost of routes: {}", Arrays.toString(distances));
		// Assign the selected customer to the vehicle route that results in the minimum
		// cost increase.
		for (int i = 0; i < routes.size(); i++) {
			int minRoute = BasicOperations.getNthMinValueIndex(distances, i);
			long[] updatedRoute = ArrayUtils.add(routes.get(minRoute), customer);
			long overcap = getRouteOverCap(updatedRoute, nodes, capacity);
			if (logger.isTraceEnabled())
				logger.trace("Checking route[{}] - OverCap: {} - {}", minRoute, overcap, Arrays.toString(updatedRoute));
			// If the capacity constraint is violated, the customer cannot be added to the
			// chosen route. The customer might be reassigned to a different route.
			if (overcap == 0) {
				logger.trace("Chosen feasible route: {}", minRoute);
				return minRoute;
			}
		}
		// If no feasible assignment can be found for the selected customer without
		// exceeding capacity reevaluate the customer selection.
		// Default: The customer is then assigned to the route that results in the
		// minimum cost increase.
		logger.trace("No feasible route for customer: {}", customer);
		return BasicOperations.getMinValueIndex(distances);
	}

}
