package mx.com.lestradam.algorithms.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.GeneticParameters;
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
		parameters.setNumFleet( Integer.valueOf(environment.getProperty("params.num-fleet")) );
		parameters.setNumGenerations( Integer.valueOf(environment.getProperty("params.num-generations")) );
		parameters.setFleetCapacity( Integer.valueOf(environment.getProperty("params.fleet-capacity")) );
		parameters.setPopulationSize( Integer.valueOf(environment.getProperty("params.population-size")) );
		parameters.setElitismCount( Integer.valueOf(environment.getProperty("params.genetic.elitism-count")) );
		parameters.setMutationRate( Double.valueOf(environment.getProperty("params.genetic.mutation-rate")) );
		parameters.setCrossoverRate( Double.valueOf(environment.getProperty("params.genetic.crossover-rate")) );		
		return parameters;
	}

}
