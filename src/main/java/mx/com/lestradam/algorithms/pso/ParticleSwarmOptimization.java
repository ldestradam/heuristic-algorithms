package mx.com.lestradam.algorithms.pso;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.PSOSolution;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.builders.SBParticleSwarmOptimization;
import mx.com.lestradam.algorithms.functions.fitness.FFParticleSwarmOptimization;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class ParticleSwarmOptimization {

	private static Logger logger = LoggerFactory.getLogger(ParticleSwarmOptimization.class);
	private PSOSolution gBestPosition;
	private List<PSOSolution> particules;

	@Autowired
	private PSOParameters psoParameters;

	@Autowired
	private SBParticleSwarmOptimization solutionBuilder;

	@Autowired
	@Qualifier("FFParticleSwarmOptimization")
	private FFParticleSwarmOptimization fitnessFunc;

	public void initial() {
		logger.debug("Creating initial population...");
		int numParticules = psoParameters.getNumParticles();
		particules = new ArrayList<>();
		for (int i = 0; i < numParticules; i++) {
			double[] position = solutionBuilder.createRandomPosition();
			double[] velocity = solutionBuilder.createRandomVelocity();
			long[] solution = solutionBuilder.encodePosition(position);
			double fitness = fitnessFunc.evaluateSolution(solution);
			PSOSolution particle = new PSOSolution(position, velocity);
			particle.setSolution(solution);
			particle.setBestPosition(position);
			particle.setFitness(fitness);
			particle.setFitnessBestPosition(fitness);
			particules.add(particle);
			if (logger.isDebugEnabled())
				logger.debug("Particle[{}] created: {}", i, particle);
		}
		double[] fitnesses =  particules.stream().mapToDouble(PSOSolution::getFitness).toArray();
		int minParticleIndex = BasicOperations.getMinValueIndex(fitnesses);
		PSOSolution particle = particules.get(minParticleIndex);
		gBestPosition = new PSOSolution(particle.getPosition(), particle.getVelocity());
		gBestPosition.setSolution(particle.getSolution());
		gBestPosition.setFitness(particle.getFitness());
		if (logger.isDebugEnabled())
			logger.debug("Best Particle[{}]: {}", minParticleIndex, gBestPosition);
	}

	public List<PSOSolution> execute() {
		initial();
		int iteration = 1;
		while (iteration < psoParameters.getNumIterations()) {
			LogWriter.printCurrentIterationPso(particules, gBestPosition, iteration);
			for (int i = 0; i < particules.size(); i++) {
				PSOSolution particle = particules.get(i); 
				double[] iVelocity = fitnessFunc.updateVelocity(particle.getPosition(), particle.getVelocity(),
						particle.getBestPosition(), gBestPosition.getPosition());
				double[] iPosition = fitnessFunc.updatePosition(particle.getPosition(), iVelocity);
				long[] iSolution = solutionBuilder.encodePosition(iPosition);
				double iFitness = fitnessFunc.evaluateSolution(iSolution);
				particle.setPosition(iPosition);
				particle.setVelocity(iVelocity);
				particle.setSolution(iSolution);
				particle.setFitness(iFitness);
				if (iFitness < particle.getFitnessBestPosition()) {
					logger.debug("Current Local Best Particle[{}] : {}", i, particle.getFitnessBestPosition());
					logger.debug("New Local Best Particle[{}] : {}", i, particle.getFitness());
					particle.setBestPosition(iPosition);
					particle.setFitnessBestPosition(iFitness);
				}
				if (iFitness < gBestPosition.getFitness()) {
					logger.debug("Current Global Best Particle[{}] : {}", i, gBestPosition.getFitness());
					logger.debug("New Global Best Particle[{}] : {}", i, particle.getFitness());
					gBestPosition = new PSOSolution(iPosition, iVelocity);
					gBestPosition.setSolution(iSolution);
					gBestPosition.setFitness(iFitness);
				}
				if (logger.isDebugEnabled())
					logger.debug("Particle updated[{}]: {}", i, particle);
			}
			iteration++;
		}
		return particules;
	}

}
