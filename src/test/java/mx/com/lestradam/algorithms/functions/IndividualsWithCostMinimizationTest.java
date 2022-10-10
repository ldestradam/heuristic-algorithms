package mx.com.lestradam.algorithms.functions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.com.lestradam.algorithms.constants.TestConstants;
import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.GeneticParameters;
import mx.com.lestradam.algorithms.data.Node;
import mx.com.lestradam.algorithms.elements.Individual;

@ExtendWith(MockitoExtension.class)
class IndividualsWithCostMinimizationTest {
	
	@Mock
	private DataSet dataset;
	
	@Mock
	private GeneticParameters parameters;
	
	@InjectMocks
	private IndividualsWithCostMinimization indCreator; 
	
	@BeforeEach
	void setUp() {		
		when(dataset.getDepot()).thenReturn(new Node(0,"Depot 0",0));		
	}
	
	@Test
	void testCreateIndividualSet1() {
		when(parameters.getNumFleet()).thenReturn(TestConstants.SET1_NUM_FLEETS);
		when(dataset.getNodes()).thenReturn(TestConstants.SET1_NODES);
		when(dataset.getEdges()).thenReturn(TestConstants.SET1_EDGES);
		Individual ind = indCreator.createIndividual();
		assertEquals(ind.getChromosome().length, TestConstants.SET1_NUM_FLEETS + TestConstants.SET1_NUM_NODES);
	}
	
	@Test
	void testCreateIndividualSet2() {
		when(parameters.getNumFleet()).thenReturn(TestConstants.SET2_NUM_FLEETS);
		when(dataset.getNodes()).thenReturn(TestConstants.SET2_NODES);
		when(dataset.getEdges()).thenReturn(TestConstants.SET2_EDGES);
		Individual ind = indCreator.createIndividual();
		assertEquals(ind.getChromosome().length, TestConstants.SET2_NUM_FLEETS + TestConstants.SET1_NUM_NODES);
	}

}
