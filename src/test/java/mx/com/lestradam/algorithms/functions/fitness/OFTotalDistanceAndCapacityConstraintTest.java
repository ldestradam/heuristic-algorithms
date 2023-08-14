package mx.com.lestradam.algorithms.functions.fitness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import mx.com.lestradam.algorithms.constants.TestConstants;
import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.functions.fitness.OFTotalDistanceAndCapacityConstraint;

@ExtendWith(MockitoExtension.class)
class OFTotalDistanceAndCapacityConstraintTest {
	
	private static final long FLEET_CAPACITY = 15;
	private static final double PENALTY = 0.5;
	
	@Mock
	private DataSet dataset;
	
	@Mock
	private AlgorithmsParameters params;
	
	@InjectMocks
	private OFTotalDistanceAndCapacityConstraint objectiveFunction;
	
	@BeforeEach
	void setUp() {
		when(dataset.getDepot()).thenReturn(new Node(0,"Depot 0",0));
		when(dataset.getEdges()).thenReturn(TestConstants.SET1_EDGES);
		when(dataset.getNodes()).thenReturn(TestConstants.SET1_NODES);
		when(params.getFleetCapacity()).thenReturn(FLEET_CAPACITY);
		when(params.getCapacityPenalty()).thenReturn(PENALTY);
		objectiveFunction.init();
	}

	@Test
	void evaluateSolutionWithPenaltyTest() {
		long[] solution = {0,1,2,0,3,4,0,5};
		long expectedObjValue = 43;
		long actualObjValue = objectiveFunction.evaluate(solution);
		assertEquals(expectedObjValue, actualObjValue);
	}
	
	@Test
	void evaluateSolutionTest() {
		long[] solution = {0,1,0,3,4,0,5};
		long expectedObjValue = 35;
		long actualObjValue = objectiveFunction.evaluate(solution);
		assertEquals(expectedObjValue, actualObjValue);
	}

}
