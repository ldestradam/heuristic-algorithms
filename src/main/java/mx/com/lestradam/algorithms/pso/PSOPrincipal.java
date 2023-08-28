package mx.com.lestradam.algorithms.pso;

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
import mx.com.lestradam.algorithms.functions.fitness.FFParticleSwarmOptimization;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class PSOPrincipal {
	
	private static final Logger logger = LoggerFactory.getLogger(PSOPrincipal.class);
	
	@Autowired
	private PSOParameters params;
	
	@Autowired
	@Qualifier("FFParticleSwarmOptimization")
	private FFParticleSwarmOptimization fitnessFunc;
	
	@Autowired
	private ParticleSwarmOptimization pso;
	
	@Autowired
	private PSOMultithread psoMultithread;
	
	public SolutionSet run() {
		LogWriter.printPsoParameters(params);
		long startTime = System.nanoTime();
		List<PSOSolution> solutions = pso.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		SolutionSet setSolutions = generateSolutionSet(solutions);
		LogWriter.printPopulation(setSolutions, duration);
		return setSolutions;
	}

	public SolutionSet run(int numThread) {
		LogWriter.printPsoParameters(params);
		logger.info("RUNNING ALGORITHM WITH {} THREADS.", numThread);
		long startTime = System.nanoTime();
		List<PSOSolution> solutions = psoMultithread.execute(numThread);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		SolutionSet setSolutions = generateSolutionSet(solutions);
		LogWriter.printPopulation(setSolutions, duration);
		return setSolutions;
	}
	
	private SolutionSet generateSolutionSet(final List<PSOSolution> particules) {
		int numParticules = params.getNumParticles();
		long totalFitness = 0;
		SolutionSet solutionSet = new SolutionSet(numParticules);
		for (int i = 0; i < numParticules; i++) {
			totalFitness += particules.get(i).getFitness();
			long excess = fitnessFunc.excess(particules.get(i).getSolution());
			Solution solution = new Solution(particules.get(i).getSolution(), particules.get(i).getFitness(), excess);
			solutionSet.setSolution(i, solution);
		}
		solutionSet.setFitness(totalFitness);
		return solutionSet;
	}

}
