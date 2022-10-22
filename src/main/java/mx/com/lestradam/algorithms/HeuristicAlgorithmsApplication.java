package mx.com.lestradam.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mx.com.lestradam.algorithms.data.AlgorithmsParameters;
import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.genetic.GeneticAlgorithm;

@SpringBootApplication
public class HeuristicAlgorithmsApplication implements CommandLineRunner{
	
	private static Logger logger = LoggerFactory.getLogger(HeuristicAlgorithmsApplication.class);
	
	@Autowired
	private GeneticAlgorithm genetic;
	
	@Autowired
	private GeneticParameters geneticParams;
	
	@Autowired
	private AlgorithmsParameters parameters;

	public static void main(String[] args) {
		logger.info("STARTING THE APPLICATION");
		SpringApplication.run(HeuristicAlgorithmsApplication.class, args);
		logger.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {		
		printParameters();
		Population population = genetic.execute();
		printPopulation(population);
	}
	
	private void printParameters() {
		logger.info("PARAMETERS CONFIGURATION");
		logger.info("FLEET CAPACITY: {}", parameters.getFleetCapacity());
		logger.info("NUM. OF FLEETS: {}", parameters.getNumFleet());
		logger.info("POPULATION SIZE: {}", geneticParams.getPopulationSize());		
		logger.info("NUM. OF GENERATION: {}", geneticParams.getNumGenerations());
		logger.info("CROSSOVER RATE: {}", geneticParams.getCrossoverRate());		
		logger.info("MUTATION RATE: {}", geneticParams.getMutationRate());
	}
	
	private void printPopulation(Population population) {
		logger.info("FINAL RESULTS");
		logger.info("POPULATION FITNESS: {}", population.getPopulationFitness());
		for(Individual individual : population.getIndividuals()) {
			logger.info("{}", individual);
		}
	}

}
