package mx.com.lestradam.algorithms.functions;

import java.util.ArrayList;
import java.util.Arrays;
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
		List<long[]> routes = new ArrayList<>();
		long cost = 0;
		int depotInd = 0;
		int offset = 0;
		do {
			depotInd = BasicOperations.getNextDepot(chromosome, offset, depot.getId());
			routes.add(Arrays.copyOfRange(chromosome, offset, depotInd));
			offset = depotInd;
		} while (offset != 0);
		for(long[] route: routes)
			cost += BasicFitnessOperations.getDistanceRoute(route, edges);
		return cost;
	}

}
