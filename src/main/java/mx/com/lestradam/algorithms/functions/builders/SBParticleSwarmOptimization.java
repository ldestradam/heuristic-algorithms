package mx.com.lestradam.algorithms.functions.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

@Component
public class SBParticleSwarmOptimization {

	private static Logger logger = LoggerFactory.getLogger(SBParticleSwarmOptimization.class);

	@Autowired
	private DataSet dataset;

	@Autowired
	private AlgorithmsParameters parameters;

	public double[] createRandomPosition() {
		int size = dataset.getNodes().size() - 1;
		double[] position = BasicOperations.generateRandomArrayNumbers(size);
		if (logger.isTraceEnabled())
			logger.trace("Position created: {}", Arrays.toString(position));
		return position;
	}

	public double[] createRandomVelocity() {
		int size = dataset.getNodes().size() - 1;
		double[] velocity = BasicOperations.generateRandomArrayNumbers(size);
		if (logger.isTraceEnabled())
			logger.trace("Velocity created: {}", Arrays.toString(velocity));
		return velocity;
	}

	public long[] encodePosition(final double[] position) {
		List<Node> actualCustomers = dataset.getNodes().stream().filter(c -> c.getId() != dataset.getDepot().getId())
				.collect(Collectors.toList());
		if (logger.isTraceEnabled()) {
			logger.trace("Encoding position: {}", Arrays.toString(position));
		}
		List<long[]> routes = new ArrayList<>();
		for (int i = 0; i < parameters.getNumFleet(); i++)
			routes.add(new long[] { dataset.getDepot().getId() });
		for (int i = 0; i < position.length; i++) {
			int index = BasicOperations.findNthSmallestIndex(position, i);
			long costumer = actualCustomers.get(index).getId();
			int routeInd = RoutesOperations.getFeasibleRouteIndex(routes, dataset.getEdges(), dataset.getNodes(),
					costumer, parameters.getFleetCapacity());
			long[] updatedRoute = ArrayUtils.add(routes.get(routeInd), costumer);
			routes.set(routeInd, updatedRoute);
			if (logger.isTraceEnabled()) {
				logger.trace("Costumer[{}]:{} - Route[{}]: {}", index, costumer, routeInd,
						Arrays.toString(updatedRoute));
			}
		}
		if (logger.isTraceEnabled()) {
			for (int i = 0; i < routes.size(); i++)
				logger.trace("Route[{}] :{}", i, Arrays.toString(routes.get(i)));
		}
		long[] solution = RoutesOperations.generateSolutionFromRoutes(routes);
		if (logger.isTraceEnabled()) {
			logger.trace("Position[{}]: {}", position.length, Arrays.toString(position));
			logger.trace("Position encoded[{}]: {}", solution.length, Arrays.toString(solution));
		}
		return solution;
	}

}
