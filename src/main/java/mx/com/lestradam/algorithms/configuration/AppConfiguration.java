package mx.com.lestradam.algorithms.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.data.Node;
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
	GeneticParameters geneticParameters(Environment environment, DataSet dataset) {
		List<Node> nodes = dataset.getNodes();
		long totalRequest = 0;
		long fleetCapacity = Integer.valueOf(environment.getProperty("params.fleet-capacity"));
		for (Node node : nodes)
			totalRequest += node.getQuantity();
		int totalFleets = (int)(totalRequest / fleetCapacity + (totalRequest % fleetCapacity == 0 ? 0 : 1));
		GeneticParameters parameters = new GeneticParameters();
		parameters.setNumFleet( totalFleets );
		parameters.setFleetCapacity( fleetCapacity );
		parameters.setNumGenerations( Integer.valueOf(environment.getProperty("params.num-generations")) );		
		parameters.setPopulationSize( Integer.valueOf(environment.getProperty("params.population-size")) );
		parameters.setElitismCount( Integer.valueOf(environment.getProperty("params.genetic.elitism-count")) );
		parameters.setMutationRate( Double.valueOf(environment.getProperty("params.genetic.mutation-rate")) );
		parameters.setCrossoverRate( Double.valueOf(environment.getProperty("params.genetic.crossover-rate")) );		
		return parameters;
	}

}
