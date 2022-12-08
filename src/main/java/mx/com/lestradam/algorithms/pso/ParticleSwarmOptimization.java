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
import mx.com.lestradam.algorithms.elements.Solution;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.functions.builders.SBParticleSwarmOptimization;
import mx.com.lestradam.algorithms.functions.fitness.FFParticleSwarmOptimization;

@Component
public class ParticleSwarmOptimization {
	
	private static Logger logger = LoggerFactory.getLogger(ParticleSwarmOptimization.class);
	private List<PSOSolution> particules;
	private PSOSolution gBestPosition;
	
	@Autowired
	private PSOParameters psoParameters;
	
	@Autowired
	private SBParticleSwarmOptimization solutionBuilder;
	
	@Autowired
	@Qualifier("FFParticleSwarmOptimization")
	private FFParticleSwarmOptimization fitnessFunc;
	
	public void initial() {
		int numParticules = psoParameters.getNumParticles();
		particules = new ArrayList<>();
		for(int i = 0; i < numParticules; i++) {
			double[] position = solutionBuilder.generateRandomArrayNumbers();
			double[] velocity = solutionBuilder.generateRandomArrayNumbers();
			long[] solution = solutionBuilder.encodePosition(position);
			long fitness = fitnessFunc.evaluateSolution(solution);
			PSOSolution particle = new PSOSolution(position, velocity);
			particle.setSolution(solution);
			particle.setBestPosition(position);
			particle.setFitness(fitness);
			particle.setFitnessBestPosition(fitness);
			particules.add(particle);
		}
		long minCost = particules.get(0).getFitness();
		int minParticleIndex = 0;
		for(int i = 0; i < numParticules; i++) {
			if(particules.get(i).getFitness() < minCost) {
				minCost = particules.get(i).getFitness();
				minParticleIndex = i;
			}
		}
		PSOSolution particle = particules.get(minParticleIndex);
		gBestPosition = new PSOSolution(particle.getPosition(), particle.getVelocity());
		gBestPosition.setSolution(particle.getSolution());
		gBestPosition.setFitness(particle.getFitness());
	}
	
	public SolutionSet generateSolutionSet() {
		int numParticules = psoParameters.getNumParticles();
		SolutionSet solutionSet = new SolutionSet(numParticules);
		for(int i = 0; i < numParticules; i++) {
			Solution solution = new Solution(particules.get(i).getSolution(), particules.get(i).getFitness());
			solutionSet.setSolution(i, solution);
		}
		Solution bestSolution = new Solution(gBestPosition.getSolution(), gBestPosition.getFitness());
		solutionSet.setSolution(numParticules, bestSolution);
		return solutionSet;
	} 
	
	public SolutionSet execute() {
		initial();
		int iteration = 0;		
		while(iteration < psoParameters.getNumIterations()) {
			PSOSolution particle = particules.get(iteration);
			double[] iVelocity = fitnessFunc.updateVelocity(particle.getPosition(), particle.getVelocity(), particle.getBestPosition(), gBestPosition.getPosition());
			double[] iPosition = fitnessFunc.updatePosition(particle.getPosition(), iVelocity);
			long[] iSolution = solutionBuilder.encodePosition(iPosition);
			long iFitness = fitnessFunc.evaluateSolution(iSolution);
			particules.get(iteration).setPosition(iPosition);
			particules.get(iteration).setVelocity(iVelocity);
			particules.get(iteration).setSolution(iSolution);
			particules.get(iteration).setFitness(iFitness);
			if(iFitness < particle.getFitnessBestPosition()) {
				particules.get(iteration).setBestPosition(iPosition);
				particules.get(iteration).setFitnessBestPosition(iFitness);
			}
			if(iFitness < gBestPosition.getFitness()) {
				gBestPosition = new PSOSolution(iPosition, iVelocity);
				gBestPosition.setSolution(iSolution);
				gBestPosition.setFitness(iFitness);
			}
			iteration++;
		}
		return generateSolutionSet();
	} 

}
