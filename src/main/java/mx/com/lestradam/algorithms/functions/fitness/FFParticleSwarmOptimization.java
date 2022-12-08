package mx.com.lestradam.algorithms.functions.fitness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.Solution;

@Component("FFParticleSwarmOptimization")
public class FFParticleSwarmOptimization {
	
	@Autowired
	private PSOParameters psoParameters;
	
	@Autowired
	private ObjectiveFunction objFunction;

	public long evaluateSolution(long[] solution) {
		return objFunction.evaluate(solution);
	}
	
	public double[] updatePosition(double[] position, double[] velocity) {
		int numParticles = psoParameters.getNumParticles();
		double[] newPosition = new double[numParticles];
		for(int i = 0; i < numParticles; i++) {
			newPosition[i] = position[i] + velocity[i];
		}
		return newPosition;
	}
	
	public double[] updateVelocity(double[] position, double[] velocity, double[] pBest, double[] gBest) {
		int w = psoParameters.getInertia();
		int c1 = psoParameters.getAccelerationC1();
		int c2 = psoParameters.getAccelerationC2();
		double rand1 = Math.random();
		double rand2 = Math.random();
		double pBestUpdate = 0.0;
		double gBestUpdate = 0.0;
		double[] velocityUpdate = new double[velocity.length];
		for(int i = 0; i < velocityUpdate.length; i++) {
			velocityUpdate[i] = velocity[i] * w; 
			pBestUpdate = (pBest[i] - position[i]) * c1 * rand1;
			gBestUpdate = gBest[i] - position[i] * c2 * rand2;
			velocityUpdate[i] = (w * velocity[i]) + pBestUpdate + gBestUpdate;
		}
		return velocityUpdate;
	}

	
}
