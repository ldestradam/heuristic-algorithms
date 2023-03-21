package mx.com.lestradam.algorithms.functions.fitness;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Edge;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Primary
@Component("OFTotalDistanceAndCapacityConstraint")
public class OFTotalDistanceAndCapacityConstraint implements ObjectiveFunction {
	
	private static Logger logger = LoggerFactory.getLogger(OFTotalDistanceAndCapacityConstraint.class);
	
	private List<Edge> edges;	
	private Node depot;
	
	@Autowired
	private DataSet dataset;
	
	@Autowired
	private AlgorithmsParameters params;
	
	@PostConstruct
	public void init() {
		edges = dataset.getEdges();
		depot = dataset.getDepot();
	}


	@Override
	public long evaluate(long[] solution) {
		long cost = 0;
		logger.trace("Solution to evaluate:{}", solution);
		List<long[]> routes = RoutesOperations.splitIntoRoute(solution, depot.getId());
		for(long[] route: routes)
			cost += RoutesOperations.getDistanceRoute(route, edges) + penalty(route);
		return cost;
	}
	
	private double penalty(long[] route) {
		long actualCapacity = 0;
		long overcapacity = 0;
		long capacity = params.getFleetCapacity();
		double penalty = params.getCapacityPenalty();
		for (int i = 0; i < route.length; i++) {
			long clientDemand = RoutesOperations.getClientDemand(route[i], dataset.getNodes());
			actualCapacity += clientDemand;
			if (actualCapacity > capacity) {
				overcapacity += clientDemand;
			}
		}
		double totalPenalty = overcapacity * penalty; 
		logger.trace("Overcapacity: {} Total penalty: {} Route :{}", overcapacity, totalPenalty, route);
		return totalPenalty;
	}

}
