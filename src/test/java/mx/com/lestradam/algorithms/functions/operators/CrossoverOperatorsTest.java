package mx.com.lestradam.algorithms.functions.operators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.com.lestradam.algorithms.elements.AlgorithmsParameters;
import mx.com.lestradam.algorithms.elements.DataSet;
import mx.com.lestradam.algorithms.elements.Node;
import mx.com.lestradam.algorithms.operators.CrossoverOperators;

@ExtendWith(MockitoExtension.class)
class CrossoverOperatorsTest {

	@Mock
	private AlgorithmsParameters parameters;

	@Mock
	private DataSet dataset;

	@InjectMocks
	private CrossoverOperators crossover;

	@BeforeEach
	void setUp() {
		when(dataset.getNodes()).thenReturn(
				Arrays.asList(new Node(1, "Sede 1", 15), new Node(2, "Sede 2", 7), new Node(3, "Sede 3", 10),
						new Node(4, "Sede 4", 5), new Node(5, "Sede 5", 13), new Node(0, "Depot 1", 0)));
		when(dataset.getDepot()).thenReturn(new Node(0, "Depot 0", 0));
		when(parameters.getNumFleet()).thenReturn(3);
		when(parameters.getFleetCapacity()).thenReturn(20L);
	}

	@Test
	void testOrderCrossover() {
		long[] parent1 = { 0, 1, 2, 0, 3, 4, 0, 5 };
		long[] parent2 = { 0, 2, 3, 0, 1, 5, 0, 4 };
		List<long[]> offsprings = crossover.orderCrossover(parent1, parent2);
		assertTrue(parent1.length == offsprings.get(0).length && parent1.length == offsprings.get(1).length);
	}

	@Test
	void testOrderCrossoverNoRepeatingElements() {
		long[] parent1 = { 0, 1, 2, 0, 3, 4, 0, 5 };
		long[] parent2 = { 0, 2, 3, 0, 1, 5, 0, 4 };
		List<long[]> offsprings = crossover.orderCrossover(parent1, parent2);
		boolean repeated = false;
		for (int i = 0; i < offsprings.size(); i++) {
			long[] offspring = offsprings.get(i);
			Set<Long> tempSet = new HashSet<>();
			for (int j = 0; j < offspring.length; j++) {
				if (offspring[j] == 0) // DETPOT
					continue;
				if (!tempSet.add(offspring[j])) {
					repeated = true;
					break;
				}
			}
		}
		assertFalse(repeated);
	}

}
