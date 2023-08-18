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
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

/**
 * Randomized Greedy Construction initialization method for the CVRP.
 * 
 * In this method, an initial solution is constructed by iteratively selecting
 * one customer at a time and assigning it to one of the available vehicle
 * routes. The customer selection is done randomly, and once a customer is
 * chosen, it is assigned to the vehicle route that minimizes the cost of adding
 * that customer to the current set of routes.
 * 
 * The process is repeated until all customers are assigned to vehicle routes.
 * This method combines random selection with a greedy approach to make routing
 * decisions based on minimizing the immediate cost while constructing the
 * initial solution.
 * 
 * @author leonardo estrada
 *
 */
@Component("RandomizedGreedy")
public class RandomizedGreedyConstruction implements SolutionBuilder {

	private static Logger logger = LoggerFactory.getLogger(RandomizedGreedyConstruction.class);

	private Random rand = new Random();

	@Autowired
	private DataSet dataset;

	@Autowired
	private AlgorithmsParameters parameters;

	@Override
	public long[] createSolution() {
		logger.trace("Building initial solution ...");
		long depot = dataset.getDepot().getId();
		List<Long> unassignedCustomers = dataset.getNodes().stream().filter(customer -> customer.getId() != depot)
				.map(Node::getId).collect(Collectors.toList());
		// Start with an empty solution, where no customers are assigned to any route.
		List<long[]> routes = new ArrayList<>();
		for (int i = 0; i < parameters.getNumFleet(); i++)
			routes.add(new long[] { depot });
		do {
			// Randomly select one customer from the set of unassigned customers.
			int customerInd = rand.nextInt(unassignedCustomers.size());
			// Evaluate the cost of adding the selected customer to each of the available
			// vehicle routes.
			int routeInd = getFeasibleRouteIndex(routes, unassignedCustomers.get(customerInd));
			// Assign the selected customer to the vehicle route that results in the minimum
			// cost increase.
			long[] updatedRoute = ArrayUtils.add(routes.get(routeInd), unassignedCustomers.get(customerInd));
			routes.set(routeInd, updatedRoute);
			unassignedCustomers.remove(customerInd);
		} while (!unassignedCustomers.isEmpty()); // Repeat until all customers are assigned to vehicle routes.
		long[] solution = RoutesOperations.generateSolutionFromRoutes(routes);
		if (logger.isTraceEnabled())
			logger.trace("Solution created: {}", Arrays.toString(solution));
		return solution;
	}

	private int getFeasibleRouteIndex(final List<long[]> routes, final long customer) {
		logger.trace("Checking feasible route for customer: {}", customer);
		long[] distances = new long[routes.size()];
		// Evaluate the cost of adding the selected customer to each of the available
		// vehicle routes.
		for (int i = 0; i < routes.size(); i++) {
			long[] expectedRoute = ArrayUtils.add(routes.get(i), customer);
			distances[i] = RoutesOperations.getDistanceRoute(expectedRoute, dataset.getEdges());
		}
		if (logger.isTraceEnabled())
			logger.trace("Expected cost of routes: {}", Arrays.toString(distances));
		// Assign the selected customer to the vehicle route that results in the minimum
		// cost increase.
		for (int i = 0; i < routes.size(); i++) {
			int minRoute = BasicOperations.getNthMinValueIndex(distances, i);
			long[] updatedRoute = ArrayUtils.add(routes.get(minRoute), customer);
			long overcap = RoutesOperations.getRouteOverCap(updatedRoute, dataset.getNodes(),
					parameters.getFleetCapacity());
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
