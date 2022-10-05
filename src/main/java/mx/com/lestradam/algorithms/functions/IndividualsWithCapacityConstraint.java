package mx.com.lestradam.algorithms.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.data.Node;
import mx.com.lestradam.algorithms.elements.Individual;


/**
 * Individuals with capacity constraint
 * 
 * The solution is constructed by assigning one customer at a time to one of the m vehicle routes.
 * The selection of the customer is randomly made. The customer is then assigned to the route that
 * meets capacity constraint. If the client does not meet the constraint, it is queued for the next route.
 * The above procedure is repeated until all customers are routed.
 * 
 * @author leonardo estrada
 *
 */
@Component("IndividualsWithCapacityConstraint")
public class IndividualsWithCapacityConstraint implements IndividualCreation {
	
	private static Logger logger = LogManager.getLogger(IndividualsWithCapacityConstraint.class);
	private Random rand = new Random();	
	private List<Node> customers;
	private Node depot;
	
	@Autowired
	private DataSet dataset;
	
	@Autowired
	private GeneticParameters parameters;
	
	@PostConstruct
	public void init() {
		customers = dataset.getNodes();
		depot = dataset.getDepot();
	}

	public long getRandomCustomerId(List<Long> customers, List<Long> customersRouted) {
		long customerId = 0;
		int customerIndex = 0;
		boolean isOnRoute = false;	
		int customersLength = customers.size();
		do {
			if(logger.isDebugEnabled()) {
				logger.debug("Customers: {}", Arrays.toString(customers.stream().mapToLong(i->i).toArray()));
				logger.debug("Customers Routed: {}", Arrays.toString(customersRouted.stream().mapToLong(i->i).toArray()));
			}
			customerIndex = rand.nextInt(customersLength);
			customerId = customers.get(customerIndex);
			logger.debug("Customer id: {}", customerId);
			isOnRoute = !customersRouted.contains(customerId);
		} while (!isOnRoute);
		return customerId;
	}
	

	@Override
	public Individual createIndividual() {
		long totalCapacity = 0;	
		long depotId = depot.getId();
		List<long[]> routes = new ArrayList<>();
		List<Long> route = new ArrayList<>();
		List<Long> customersRouted = new ArrayList<>();
		List<Long> customersNotRouted = new ArrayList<>();
		List<Long> customersId = new ArrayList<>(customers.stream().filter(customer ->customer.getId() != depotId).map(customer -> customer.getId()).collect(Collectors.toList()));
		int noOfCustomers = customersId.size();
		while (customersRouted.size() < noOfCustomers) {
			if(totalCapacity == parameters.getFleetCapacity() || customersId.isEmpty()) {
				route.add(0, depotId);
				long[] routeN = route.stream().mapToLong(i->i).toArray();
				if(logger.isDebugEnabled())
					logger.debug("Route completed: {}", Arrays.toString(routeN) );
				routes.add(routeN);
				if(routes.size() > parameters.getFleetCapacity())
					throw new IndexOutOfBoundsException("Invalid solution, number of fleet exceeded: " + routes.size());
				route.clear();
				totalCapacity = 0;								
				customersId.addAll(customersNotRouted);
				customersNotRouted.clear();				
			}			
			long customerId = getRandomCustomerId(customersId, customersRouted);
			Node customer = customers.stream().filter( currentCustomer -> currentCustomer.getId() == customerId ).findFirst().orElseThrow(()-> new NullPointerException("Node not found with id " + customerId));
			if( totalCapacity + customer.getQuantity() <= parameters.getFleetCapacity() ) {
				totalCapacity += customer.getQuantity();
				route.add(customer.getId());
				customersRouted.add(customer.getId());
			}else {
				customersNotRouted.add(customer.getId());
			}
			customersId.remove(Long.valueOf(customerId));
		}		
		long[] solution = {};
		for(long[] routeAux : routes) {
			solution = ArrayUtils.addAll(solution, routeAux);
		}		
		return new Individual(solution);
	}

}
