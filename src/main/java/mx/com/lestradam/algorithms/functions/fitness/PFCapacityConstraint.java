package mx.com.lestradam.algorithms.functions.fitness;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Component("CapacityConstraint")
public class PFCapacityConstraint implements PenaltyFunction {

	private static Logger logger = LoggerFactory.getLogger(PFCapacityConstraint.class);

	@Autowired
	private DataSet dataset;

	@Autowired
	private AlgorithmsParameters params;

	@Override
	public double evaluate(long[] solution) {
		long overcapacity = RoutesOperations.getSolutionOverCap(solution, dataset.getNodes(), params.getFleetCapacity(),
				dataset.getDepot().getId());
		double penalty = overcapacity * params.getCapacityPenalty();
		if (logger.isDebugEnabled())
			logger.trace("Overcapacity: {} Penalty: {} Solution :{}", overcapacity, penalty, Arrays.toString(solution));
		return penalty;
	}

	@Override
	public double excess(long[] solution) {
		return RoutesOperations.getSolutionOverCap(solution, dataset.getNodes(), params.getFleetCapacity(),
				dataset.getDepot().getId());
	}

}
