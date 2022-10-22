package mx.com.lestradam.algorithms.functions.generation;

import mx.com.lestradam.algorithms.elements.Individual;

/**
 * Interface for the creation of individuals
 * To facilitate the exchange of implementation of creation of individuals.
 * 
 * @author leonardo estrada
 *
 */
public interface IndividualCreation {

    /**
     * Create an individual from the list of client nodes, depot and number of fleets
     * @return Representation of individual solution based on the implementation
     */
	public Individual createIndividual();
	
}
