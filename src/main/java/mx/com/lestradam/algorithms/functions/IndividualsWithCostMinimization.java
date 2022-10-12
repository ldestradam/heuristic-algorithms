package mx.com.lestradam.algorithms.functions;

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

import mx.com.lestradam.algorithms.data.AlgorithmsParameters;
import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.data.Node;
import mx.com.lestradam.algorithms.elements.Individual;

/**
 * Individuals with cost minimization
 * An initial solution is constructed by assigning one customer at a time to one of the m vehicle routes.
 * The selection of the customer is randomly made. The customer is then assigned to the location that
 * minimizes the cost of assigning this customer over the current set of vehicle routes. The above 
 * procedure is repeated until all customers are routed.
 *  
 * @author leonardo estrada
 *
 */
@Component("IndividualsWithCostMinimization")
public class IndividualsWithCostMinimization implements IndividualCreation {
	
	private static Logger logger = LoggerFactory.getLogger(IndividualsWithCostMinimization.class);
	
	private Random rand = new Random();
	
	@Autowired
	private DataSet dataset;
	
	@Autowired
	private AlgorithmsParameters parameters;
	
	@Override
	public Individual createIndividual() {
		long[] solution = {};
		List<Node> nodes = dataset.getNodes();
		List<Edge> edges = dataset.getEdges();
		Node depot = dataset.getDepot();
		int numFleet = parameters.getNumFleet();
		List<long[]> routes = new ArrayList<>();
		long[] costs = new long[numFleet];
		long[] customersId = nodes.stream().filter(node -> node.getId() != depot.getId()).mapToLong(Node::getId).toArray();
		for(int i = 0; i < numFleet; i++)
			routes.add( new long[]{depot.getId()});
		while (customersId.length > 0) {
			int customerInd = getRandomCustomerIndex(customersId, routes);			
			if(logger.isTraceEnabled()) {
				logger.trace("Selected customer: {}", customersId[customerInd]);
			}
			for(int i = 0; i < routes.size(); i++) {
				long[] expectedRoute = ArrayUtils.add(routes.get(i), customersId[customerInd]);
				costs[i] = BasicFitnessOperations.getDistanceRoute(expectedRoute, edges);
				if(logger.isTraceEnabled()) {
					logger.trace("Expected Cost[{}]: {}", i, costs[i]);
					logger.trace("Expected route[{}]: {}", i, Arrays.toString(expectedRoute));					
				}
			}
			int routeInd = BasicOperations.getMinimunCostIndex(costs);
			long[] updatedRoute = ArrayUtils.add(routes.get(routeInd), customersId[customerInd]);
			if(logger.isTraceEnabled()) {
				logger.trace("Min. route index: {}", routeInd);
				logger.trace("Selected route: {}", Arrays.toString(updatedRoute));					
			}
			routes.set(routeInd, updatedRoute);
			customersId = ArrayUtils.remove(customersId, customerInd);
		}
		for(long[] routeAux : routes)
			solution = ArrayUtils.addAll(solution, routeAux);
		if(logger.isDebugEnabled())
			logger.debug("Chromosome generated: {}", Arrays.toString(solution));
		return new Individual(solution);
	}
	
	private int getRandomCustomerIndex(long[] customersId, List<long[]> routes) {
		long customerId = 0;
		int customerIndex = 0;
		boolean isOnRoute = false;		
		int customersLength = customersId.length;
		List<Long> customersRouted = new ArrayList<>();
		for (long[] route : routes)
			customersRouted.addAll( Arrays.stream(route).boxed().collect(Collectors.toList()) );
		do {
			customerIndex = rand.nextInt(customersLength);
			customerId = customersId[customerIndex];
			isOnRoute = !customersRouted.contains(customerId);
		} while (!isOnRoute);
		return customerIndex;
	}



}
