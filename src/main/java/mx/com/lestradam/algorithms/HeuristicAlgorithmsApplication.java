package mx.com.lestradam.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mx.com.lestradam.algorithms.elements.Individual;
import mx.com.lestradam.algorithms.elements.Population;
import mx.com.lestradam.algorithms.genetic.GeneticAlgorithm;

@SpringBootApplication
public class HeuristicAlgorithmsApplication implements CommandLineRunner{
	
	private static Logger logger = LoggerFactory.getLogger(HeuristicAlgorithmsApplication.class);
	
	@Autowired
	private GeneticAlgorithm genetic;

	public static void main(String[] args) {
		logger.info("STARTING THE APPLICATION");
		SpringApplication.run(HeuristicAlgorithmsApplication.class, args);
		logger.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {		
		 Population population = genetic.execute();
		 printPopulation(population);
	}
	
	private void printPopulation(Population population) {
		logger.info("RESULTS");
		for(Individual individual : population.getIndividuals()) {
			logger.info("Individual : {}", individual);
		}
		logger.info("RESULTS");
	}

}
