package mx.com.lestradam.algorithms.functions.fitness;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Edge;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Component("OFTotalDistanceAndCapacityConstraint")
public class OFTotalDistanceAndCapacityConstraint implements ObjectiveFunction {
	
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
		List<long[]> routes = RoutesOperations.splitIntoRoute(solution, depot.getId());
		for(long[] route: routes)
			cost += RoutesOperations.getDistanceRoute(route, edges) + penalty(solution);
		return cost;
	}
	
	private long penalty(long[] solution) {
		long totalPenalty = 0;
		long capacity = params.getFleetCapacity();
		long actualCapacity = 0;
		long penalty = params.getCapacityPenalty();
		for (int i = 0; i < solution.length; i++) {
			long clientDemand = solution[i] == dataset.getDepot().getId() ? 0 : RoutesOperations.getClientDemand(solution[i], dataset.getNodes());
			actualCapacity += clientDemand;
			if (actualCapacity > capacity) {
				totalPenalty += 1;
			}
		}
		return totalPenalty * penalty;
	}

}
