package mx.com.lestradam.algorithms.genetic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class GAPrincipal {
	
	private static final Logger logger = LoggerFactory.getLogger(GAPrincipal.class);
	
	@Autowired
	private GeneticParameters params;
	
	@Autowired
	private GeneticAlgorithm ga;
	
	@Autowired
	private GAMultithread gaMultithread;
	
	public SolutionSet run() {
		LogWriter.printGeneticParameters(params);
		long startTime = System.nanoTime();
		SolutionSet solutions = ga.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		LogWriter.printPopulation(solutions, duration);
		return solutions;
	}
	
	public SolutionSet run(int numThread) {
		LogWriter.printGeneticParameters(params);
		logger.info("RUNNING ALGORITHM WITH {} THREADS.", numThread);
		long startTime = System.nanoTime();
		SolutionSet solutions = gaMultithread.execute(numThread);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		LogWriter.printPopulation(solutions, duration);
		return solutions;
	}

}
