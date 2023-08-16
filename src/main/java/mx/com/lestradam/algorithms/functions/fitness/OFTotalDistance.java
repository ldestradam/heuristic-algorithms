package mx.com.lestradam.algorithms.functions.fitness;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Edge;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Component("OFTotalDistance")
public class OFTotalDistance implements ObjectiveFunction{
	
	private List<Edge> edges;	
	private Node depot;
	
	@Autowired
	private DataSet dataset;
	
	@PostConstruct
	public void init() {
		edges = dataset.getEdges();
		depot = dataset.getDepot();
	}

	@Override
	public double evaluate(long[] solution) {
		double cost = 0;
		List<long[]> routes = RoutesOperations.splitIntoRoute(solution, depot.getId());
		for(long[] route: routes)
			cost += RoutesOperations.getDistanceRoute(route, edges);
		return cost;
	}

}
