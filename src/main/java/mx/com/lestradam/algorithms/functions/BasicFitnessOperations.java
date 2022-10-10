package mx.com.lestradam.algorithms.functions;

import java.util.List;
import java.util.Optional;

import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.exceptions.DataException;

public class BasicFitnessOperations {
	
	private BasicFitnessOperations() {
		throw new IllegalStateException("Utility class");
	}
	
	public static long getEdgeWeight(long source, long target, List<Edge> edges) {
		if (source == target)
			return 0;
		Optional<Edge> edgeA = edges.stream()
				.filter(currentegde -> (int) currentegde.getSource() == source && (int)currentegde.getTarget() == target)
				.findFirst();
		Optional<Edge> edgeB = edges.stream()
				.filter(currentegde -> (int) currentegde.getSource() == target && (int)currentegde.getTarget() == source)
				.findFirst();
		if(!edgeA.isPresent() && !edgeB.isPresent())
			throw new DataException("Edge not found with source: " + source + " and target: " + target + " and vicecersa");
		return edgeA.isPresent() ? edgeA.get().getWeight() : edgeB.get().getWeight(); 
	}
	
	public static long getDistanceRoute(long[] route, List<Edge> edges) {
		long sum = 0;
		for (int i = 0; i < route.length; i++) {
			long source = route[i];
			long target = (i == route.length -1 ) ? route[0] : route[i + 1];
			sum += getEdgeWeight(source, target, edges);
		}
		return sum;
	}

}
