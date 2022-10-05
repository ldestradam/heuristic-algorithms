package mx.com.lestradam.algorithms.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.data.GeneticParameters;
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
	
	private Logger logger = LoggerFactory.getLogger(IndividualsWithCostMinimization.class);
	private Random rand = new Random();
	private List<Node> nodes;
	private List<Edge> edges;
	private Node depot;
	
	@Autowired
	private DataSet dataset;
	
	@Autowired
	private GeneticParameters parameters;	
	
	@PostConstruct
	private void init() {
		nodes = dataset.getNodes();
		edges = dataset.getEdges();
		depot = dataset.getDepot();
	}
	
	@Override
	public Individual createIndividual() {
		long[] solution = {};
		List<long[]> routes = new ArrayList<>();
		long[] costs = new long[routes.size()];
		long[] customersId = nodes.stream().mapToLong(Node::getId).toArray();
		for(int i = 0; i < parameters.getNumFleet(); i++)
			routes.add( new long[]{depot.getId()});
		while (customersId.length > 0) {
			int customerInd = getRandomCustomerIndex(customersId, routes);
			for(int i = 0; i < routes.size(); i++) {
				long[] expectedRoute = ArrayUtils.add(routes.get(i), customersId[customerInd]);
				costs[i] = BasicFitnessOperations.getDistanceRoute(expectedRoute, edges);
			}
			int routeInd = BasicOperations.getMinimunCostIndex(costs);
			long[] updatedRoute = ArrayUtils.add(routes.get(routeInd), customersId[customerInd]);
			routes.set(routeInd, updatedRoute);
			ArrayUtils.remove(customersId, customerInd);
		}
		for(long[] routeAux : routes)
			solution = ArrayUtils.addAll(solution, routeAux);
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
			if(logger.isDebugEnabled()) {
				logger.debug("Random customer id: {}", customerId);
				logger.debug("Customers Routed: {}", Arrays.toString(customersRouted.stream().mapToLong(customer -> customer).toArray()));
			}
		} while (!isOnRoute);
		return customerIndex;
	}



}
