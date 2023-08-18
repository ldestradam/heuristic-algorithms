package mx.com.lestradam.algorithms.functions.fitness;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Primary
@Component("OFTotalDistanceAndCapacityConstraint")
public class OFTotalDistanceAndCapacityConstraint implements ObjectiveFunction {

	private static Logger logger = LoggerFactory.getLogger(OFTotalDistanceAndCapacityConstraint.class);

	@Autowired
	private DataSet dataset;

	@Autowired
	private PenaltyFunction penalty;

	@Override
	public double evaluate(long[] solution) {
		if (logger.isDebugEnabled())
			logger.debug("Assessing Solution:{}", Arrays.toString(solution));
		double distance = 0;
		List<long[]> routes = RoutesOperations.splitIntoRoute(solution, dataset.getDepot().getId());
		for (long[] route : routes)
			distance += RoutesOperations.getDistanceRoute(route, dataset.getEdges());
		double totalPenalty = penalty.evaluate(solution);
		double cost = distance + totalPenalty;
		logger.debug("Distance: {} - Penalty: {} - Costs: {}", distance, totalPenalty, cost);
		return cost;
	}

}
