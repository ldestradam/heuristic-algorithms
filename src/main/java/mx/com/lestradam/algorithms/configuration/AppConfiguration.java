package mx.com.lestradam.algorithms.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mx.com.lestradam.algorithms.elements.ABCParameters;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.GeneticParameters;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.elements.PSOParameters;
import mx.com.lestradam.algorithms.utils.CsvReader;

@Configuration
public class AppConfiguration {
	
	@Value("${input-file.nodes}")
	private String nodesFilePath;
	
	@Value("${input-file.edges}")
	private String edgesFilePath;
    
    @Bean
    DataSet dataset() {
    	return new DataSet(CsvReader.retrieveNodeFromCsv(nodesFilePath), CsvReader.retrieveEdgesFromCsv(edgesFilePath));
    }
	
	@Bean
	GeneticParameters geneticParameters(Environment environment) {
		GeneticParameters parameters = new GeneticParameters();
		parameters.setMutationRate( Double.valueOf(environment.getProperty("params.genetic.mutation-rate")) );
		parameters.setCrossoverRate( Double.valueOf(environment.getProperty("params.genetic.crossover-rate")) );
		parameters.setNumGenerations( Integer.valueOf(environment.getProperty("params.genetic.num-generations")) );
		parameters.setPopulationSize( Integer.valueOf(environment.getProperty("params.genetic.population-size")) );
		parameters.setElitism( Integer.valueOf(environment.getProperty("params.genetic.elitism")) );
		return parameters;
	}
	
	@Bean
	AlgorithmsParameters algorithmsParameters(Environment environment, DataSet dataset) {
		AlgorithmsParameters parameters = new AlgorithmsParameters();
		List<Node> nodes = dataset.getNodes();
		long totalRequest = 0;
		long fleetCapacity = Integer.valueOf(environment.getProperty("params.fleet-capacity"));
		double capacityPenalty = Double.parseDouble(environment.getProperty("params.capacity-penalty")); 
		for (Node node : nodes)
			totalRequest += node.getQuantity();
		int totalFleets = (int)(totalRequest / fleetCapacity + (totalRequest % fleetCapacity == 0 ? 0 : 1));
		parameters.setNumFleet( totalFleets );
		parameters.setFleetCapacity( fleetCapacity );
		parameters.setCapacityPenalty(capacityPenalty);
		return parameters;
	}
	
	@Bean
	ABCParameters abcParameters(Environment environment) {
		ABCParameters parameters = new ABCParameters();
		parameters.setFoodSourceSize(Integer.valueOf(environment.getProperty("params.abc.food-source-size")));
		parameters.setImprovedLimit(Integer.valueOf(environment.getProperty("params.abc.improved-limit")));
		parameters.setNumIterations(Integer.valueOf(environment.getProperty("params.abc.num-iterations")));
		parameters.setOnlookersBees(Integer.valueOf(environment.getProperty("params.abc.onlookers-bees")));
		return parameters; 
	}
	
	@Bean
	PSOParameters psoParameters(Environment environment) {
		PSOParameters parameters = new PSOParameters();		
		parameters.setNumIterations(Integer.valueOf(environment.getProperty("params.pso.num-iterations")));
		parameters.setAccelerationC1(Float.valueOf(environment.getProperty("params.pso.acceleration-c1")));
		parameters.setAccelerationC2(Float.valueOf(environment.getProperty("params.pso.acceleration-c2")));
		parameters.setInertia(Float.valueOf(environment.getProperty("params.pso.inertia")));
		parameters.setNumParticles(Integer.valueOf(environment.getProperty("params.pso.particles")));
		return parameters; 
	}

}
