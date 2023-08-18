package mx.com.lestradam.algorithms.functions.fitness;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.functions.basic.RoutesOperations;

@Primary
@Component("CapacityConstraint")
public class PFCapacityConstraint implements PenaltyFunction {

	private static Logger logger = LoggerFactory.getLogger(PFCapacityConstraint.class);

	@Autowired
	private DataSet dataset;

	@Autowired
	private AlgorithmsParameters params;

	@Override
	public double evaluate(long[] solution) {
		if (logger.isDebugEnabled())
			logger.trace("Checking if feasible solution: {}", Arrays.toString(solution));
		long overcapacity = RoutesOperations.getSolutionOverCap(solution, dataset.getNodes(), params.getFleetCapacity(),
				dataset.getDepot().getId());
		double penalty = overcapacity * params.getCapacityPenalty();
		logger.trace("Overcapacity: {} Penalty: {} ", overcapacity, penalty);
		return penalty;
	}

	@Override
	public long excess(long[] solution) {
		return RoutesOperations.getSolutionOverCap(solution, dataset.getNodes(), params.getFleetCapacity(),
				dataset.getDepot().getId());
	}

}
