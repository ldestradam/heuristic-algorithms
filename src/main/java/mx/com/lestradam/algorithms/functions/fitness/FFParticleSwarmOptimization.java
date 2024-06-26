package mx.com.lestradam.algorithms.functions.fitness;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.PSOParameters;

@Component("FFParticleSwarmOptimization")
public class FFParticleSwarmOptimization {
	
	private static final Logger logger = LoggerFactory.getLogger(FFParticleSwarmOptimization.class);
	
	@Autowired
	private PSOParameters psoParameters;
	
	@Autowired
	private ObjectiveFunction objFunction;
	
	@Autowired
	private PenaltyFunction penalty;

	public double evaluateSolution(long[] solution) {
		return objFunction.evaluate(solution);
	}
	
	public long excess(long[] solution) {
		return penalty.excess(solution);
	}
	
	public double[] updatePosition(double[] position, double[] velocity) {
		logger.trace("Updating position ...");
		double[] newPosition = new double[position.length];		
		for(int i = 0; i < position.length; i++) {
			newPosition[i] = position[i] + velocity[i];
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Position: {}", Arrays.toString(position));
			logger.trace("Velocity: {}", Arrays.toString(velocity));
			logger.trace("New position: {}", Arrays.toString(newPosition));
		}
		return newPosition;
	}
	
	public double[] updateVelocity(double[] position, double[] velocity, double[] pBest, double[] gBest) {
		logger.trace("Updating velocity ...");
		float w = psoParameters.getInertia();
		float c1 = psoParameters.getAccelerationC1();
		float c2 = psoParameters.getAccelerationC2();
		double pBestUpdate;
		double gBestUpdate;
		double[] velocityUpdated = new double[velocity.length];
		for(int i = 0; i < velocityUpdated.length; i++) {
			double rand1 = Math.random();
			double rand2 = Math.random();
			pBestUpdate = (pBest[i] - position[i]) * c1 * rand1;
			gBestUpdate = (gBest[i] - position[i]) * c2 * rand2;
			velocityUpdated[i] = (w * velocity[i]) + pBestUpdate + gBestUpdate;
			logger.trace("W[{}] C1[{}] C2[{}] Rand1[{}] Rand2[{}]", w, c1, c2, rand1, rand2);
			logger.trace("[{}]: lBest: {} gBest: {} V: {}", i, pBestUpdate, gBestUpdate, velocityUpdated);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Position: {}", Arrays.toString(position));
			logger.trace("Velocity: {}", Arrays.toString(velocity));
			logger.trace("pBest: {}", Arrays.toString(pBest));
			logger.trace("gBest: {}", Arrays.toString(gBest));
			logger.trace("New velocity: {}", Arrays.toString(velocityUpdated));
		}
		return velocityUpdated;
	}

	
}
