package mx.com.lestradam.algorithms.pso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.elements.PSOSolution;
import mx.com.lestradam.algorithms.exceptions.AlgorithmExecutionException;
import mx.com.lestradam.algorithms.functions.basic.BasicOperations;
import mx.com.lestradam.algorithms.functions.builders.SBParticleSwarmOptimization;
import mx.com.lestradam.algorithms.functions.fitness.FFParticleSwarmOptimization;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class PSOMultithread {

	private static Logger logger = LoggerFactory.getLogger(PSOMultithread.class);
	private int numThreads;
	private PSOSolution gBestPosition;
	private ExecutorService threadPool;
	private List<PSOSolution> particules;

	@Autowired
	private PSOParameters params;

	@Autowired
	private SBParticleSwarmOptimization solutionBuilder;

	@Autowired
	@Qualifier("FFParticleSwarmOptimization")
	private FFParticleSwarmOptimization fitnessFunc;

	public List<PSOSolution> execute(int numThreads) {
		this.numThreads = numThreads;
		this.threadPool = Executors.newFixedThreadPool(this.numThreads);
		initial();
		int iteration = 0;
		List<int[]> splits = BasicOperations.splitRange(this.params.getNumParticles(), numThreads);
		List<Callable<String>> callables = new ArrayList<>();
		for (int[] split : splits) {
			callables.add(updateParticles(split[0], split[1]));
		}
		LogWriter.printCurrentIterationPso(particules, gBestPosition, iteration);
		while (iteration < params.getNumIterations()) {
			List<Future<String>> futures = invokeThreads(callables);
			iteration++;
			if (logger.isTraceEnabled())
				printThreadResults(futures);
			LogWriter.printCurrentIterationPso(particules, gBestPosition, iteration);
		}
		shutdownThreadPool();
		return particules;
	}

	public void initial() {
		logger.debug("Creating initial population...");
		particules = new ArrayList<>();
		List<int[]> splits = BasicOperations.splitRange(params.getNumParticles(), this.numThreads);
		List<Callable<String>> callables = splits.stream().map(split -> createParticles(split[0], split[1]))
				.collect(Collectors.toList());
		List<Future<String>> futures = invokeThreads(callables);
		if (logger.isTraceEnabled())
			printThreadResults(futures);
		double[] fitnesses = particules.stream().mapToDouble(PSOSolution::getFitness).toArray();
		int minParticleIndex = BasicOperations.getMinValueIndex(fitnesses);
		PSOSolution particle = particules.get(minParticleIndex);
		gBestPosition = new PSOSolution(particle.getPosition(), particle.getVelocity());
		gBestPosition.setSolution(particle.getSolution());
		gBestPosition.setFitness(particle.getFitness());
		if (logger.isDebugEnabled())
			logger.debug("Best Particle[{}]: {}", minParticleIndex, gBestPosition);
	}
	
	private Callable<String> createParticles(int start, int end){
		return () -> {
			for (int i = start; i <= end; i++) {
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
			return Thread.currentThread().getName() + " Start : " + start + " End: " + end;
		};
	}

	private Callable<String> updateParticles(int start, int end) {
		return () -> {
			for (int i = start; i <= end; i++) {
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
				setBestGlobalPosition(particle, i);
				if (logger.isDebugEnabled())
					logger.debug("Particle updated: {}", particle);
			}
			return Thread.currentThread().getName() + " Start : " + start + " End: " + end;
		};
	}

	private synchronized void setBestGlobalPosition(final PSOSolution particle, final int i) {
		logger.trace("Attempting to update best solution...");
		if (particle.getFitness() < gBestPosition.getFitness()) {
			logger.debug("Current Global Best Particle[{}] : {}", i, gBestPosition.getFitness());
			logger.debug("New Global Best Particle[{}] : {}", i, particle.getFitness());
			gBestPosition = new PSOSolution(particle.getPosition(), particle.getVelocity());
			gBestPosition.setSolution(particle.getSolution());
			gBestPosition.setFitness(particle.getFitness());
			logger.trace("Best solution updated ...");
		}
	}

	private void shutdownThreadPool() {
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.trace("Threads were shutting down...");
				threadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error on shutting down threads", e);
		}
	}

	private List<Future<String>> invokeThreads(List<Callable<String>> callables) {
		try {
			return threadPool.invokeAll(callables);
		} catch (InterruptedException e) {
			throw new AlgorithmExecutionException("Error sending employed bees", e);
		}
	}

	private void printThreadResults(List<Future<String>> futures) {
		for (Future<String> future : futures) {
			try {
				logger.trace(future.get());
			} catch (InterruptedException | ExecutionException e) {
				throw new AlgorithmExecutionException("Error getting thread results", e);
			}
		}
	}

}
