package mx.com.lestradam.algorithms.functions;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.data.Node;

@Component("SimpleFitness")
public class SimpleFitness implements IndividualFitness {
		
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
	public long evaluate(long[] chromosome){
		long cost = 0;
		List<long[]> routes = BasicOperations.splitIntoRoute(chromosome, depot.getId());
		for(long[] route: routes)
			cost += BasicFitnessOperations.getDistanceRoute(route, edges);
		return cost;
	}

}
