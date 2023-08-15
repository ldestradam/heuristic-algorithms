package mx.com.lestradam.algorithms.functions.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import mx.com.lestradam.algorithms.elements.Edge;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.exceptions.DataException;

public class RoutesOperations {

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
		return (actualCapacity > capacity) ?  actualCapacity - capacity : 0;
	}
	
	public static long getSolutionOverCap(long[] solution, List<Node> clients, long capacity, long depot) {
		long overCap = 0;
		List<long[]> routes = splitIntoRoute(solution, depot);
		for (long[] route : routes) {
			overCap += getRouteOverCap(route, clients, capacity);
		}
		return overCap;
	}

}
