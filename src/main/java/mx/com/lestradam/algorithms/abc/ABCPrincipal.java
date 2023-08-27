package mx.com.lestradam.algorithms.abc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.SolutionSet;
import mx.com.lestradam.algorithms.utils.LogWriter;

@Component
public class ABCPrincipal {

	private static final Logger logger = LoggerFactory.getLogger(ABCPrincipal.class);

	@Autowired
	private ArtificialBeeColony abc;

	@Autowired
	private ABCMultithread multithreadAbc;

	@Autowired
	private ABCParameters params;

	public SolutionSet run() {
		LogWriter.printAbcParameters(params);
		long startTime = System.nanoTime();
		SolutionSet solutions = abc.execute();
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		LogWriter.printPopulation(solutions, duration);
		return solutions;
	}

	public SolutionSet run(int numThread) {
		LogWriter.printAbcParameters(params);
		logger.info("RUNNING ALGORITHM WITH {} THREADS.", numThread);
		long startTime = System.nanoTime();
		SolutionSet solutions = multithreadAbc.execute(numThread);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		LogWriter.printPopulation(solutions, duration);
		return solutions;
	}

}
