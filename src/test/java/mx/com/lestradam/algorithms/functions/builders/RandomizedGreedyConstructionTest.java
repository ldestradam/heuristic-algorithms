package mx.com.lestradam.algorithms.functions.builders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;

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

@ExtendWith(MockitoExtension.class)
class RandomizedGreedyConstructionTest {
	
	private static final long FLEET_CAPACITY = 40;
	private static final int NUM_FLEET = 3;
	
	@Mock
	private DataSet dataset;
	
	@Mock
	private AlgorithmsParameters params;
	
	@InjectMocks
	private RandomizedGreedyConstruction builder;
	
	@BeforeEach
	void setUp() {
		when(dataset.getDepot()).thenReturn(new Node(0,"Depot 0",0));
		when(dataset.getEdges()).thenReturn(TestConstants.SET2_EDGES);
		when(dataset.getNodes()).thenReturn(TestConstants.SET2_NODES);
		when(params.getFleetCapacity()).thenReturn(FLEET_CAPACITY);
		when(params.getNumFleet()).thenReturn(NUM_FLEET);
	}

	@Test
	void testCreateSolution() {
		long[] solution = builder.createSolution();
		assertEquals(8, solution.length);
	}
}
