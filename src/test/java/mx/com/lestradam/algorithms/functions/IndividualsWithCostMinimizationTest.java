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
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.builders.SBCostMinimization;

@ExtendWith(MockitoExtension.class)
class IndividualsWithCostMinimizationTest {
	
	@Mock
	private DataSet dataset;
	
	@Mock
	private AlgorithmsParameters parameters;
	
	@InjectMocks
	private SBCostMinimization indCreator; 
	
	@BeforeEach
	void setUp() {		
		when(dataset.getDepot()).thenReturn(new Node(0,"Depot 0",0));		
	}
	
	@Test
	void testCreateIndividualSet1() {
		when(parameters.getNumFleet()).thenReturn(TestConstants.SET1_NUM_FLEETS);
		when(dataset.getNodes()).thenReturn(TestConstants.SET1_NODES);
		when(dataset.getEdges()).thenReturn(TestConstants.SET1_EDGES);
		long[] solution = indCreator.createSolution();
		assertEquals(solution.length, TestConstants.SET1_NUM_FLEETS + TestConstants.SET1_NUM_NODES);
	}
	
	@Test
	void testCreateIndividualSet2() {
		when(parameters.getNumFleet()).thenReturn(TestConstants.SET2_NUM_FLEETS);
		when(dataset.getNodes()).thenReturn(TestConstants.SET2_NODES);
		when(dataset.getEdges()).thenReturn(TestConstants.SET2_EDGES);
		long[] solution = indCreator.createSolution();
		assertEquals(solution.length, TestConstants.SET2_NUM_FLEETS + TestConstants.SET1_NUM_NODES);
	}

}
