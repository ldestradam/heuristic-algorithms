package mx.com.lestradam.algorithms.functions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.com.lestradam.algorithms.constants.TestConstants;
import mx.com.lestradam.algorithms.data.DataSet;
import mx.com.lestradam.algorithms.data.Edge;
import mx.com.lestradam.algorithms.data.Node;

@ExtendWith(MockitoExtension.class)
class SimpleFitnessTest {
	
	@Mock
	private DataSet dataset;
	
	@InjectMocks
	private SimpleFitness fitness;
	
	private List<Edge> edges = TestConstants.SET1_EDGES;
	private Node depot = new Node(0,"Depot 0",0);
	
	@BeforeEach
	void setUp() {		
		when(dataset.getDepot()).thenReturn(depot);
		when(dataset.getEdges()).thenReturn(edges);
		fitness.init();
	}

	@Test
	void testEvaluate() {
		long[] chromosome = {0,1,2,0,3,0,4,5};
		long expectedCost = BasicFitnessOperations.getEdgeWeight(0, 1, edges) +
				BasicFitnessOperations.getEdgeWeight(1, 2, edges) +
				BasicFitnessOperations.getEdgeWeight(2, 0, edges) +
				BasicFitnessOperations.getEdgeWeight(0, 3, edges) +
				BasicFitnessOperations.getEdgeWeight(3, 0, edges) +
				BasicFitnessOperations.getEdgeWeight(0, 4, edges) +
				BasicFitnessOperations.getEdgeWeight(4, 5, edges) +
				BasicFitnessOperations.getEdgeWeight(5, 0, edges);
		long actualCost = fitness.evaluate(chromosome);
		assertEquals(expectedCost, actualCost);
	}

}
