package mx.com.lestradam.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mx.com.lestradam.algorithms.cli.CommandLineApplication;
import mx.com.lestradam.algorithms.exceptions.DataException;

@SpringBootApplication
public class HeuristicAlgorithmsApplication implements CommandLineRunner{
	
	private static Logger logger = LoggerFactory.getLogger(HeuristicAlgorithmsApplication.class);
	
	@Autowired
	private CommandLineApplication application;
	
	public static void main(String[] args) {
		logger.info("STARTING THE APPLICATION");
		SpringApplication.run(HeuristicAlgorithmsApplication.class, args);
		logger.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			application.execute(args);
		} catch (DataException e) {
			logger.error("Error {}", e.getMessage());
			e.printStackTrace();
		}
	}
	
}
