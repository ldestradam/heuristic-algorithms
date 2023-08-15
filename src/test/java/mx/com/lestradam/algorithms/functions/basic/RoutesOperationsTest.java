package mx.com.lestradam.algorithms.functions.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import mx.com.lestradam.algorithms.constants.TestConstants;
import mx.com.lestradam.algorithms.exceptions.DataException;

class RoutesOperationsTest {

	@ParameterizedTest
	@CsvSource({ "1,2", "2,1" })
	void testDistanceNodes(int source, int target) {
		long distance = RoutesOperations.getDistanceNodes(source, target, TestConstants.SET2_EDGES);
		assertEquals(32, distance);
	}

	@Test
	void testDistanceNodesSameClients() {
		long distance = RoutesOperations.getDistanceNodes(1, 1, TestConstants.SET1_EDGES);
		assertEquals(0, distance);
	}

	@Test
	void testDistanceNodesDataException() {
		Exception exception = assertThrows(DataException.class, () -> {
			RoutesOperations.getDistanceNodes(-1, 0, TestConstants.SET1_EDGES);
		});
		assertEquals(DataException.class, exception.getClass());
	}

	@Test
	void testDistanceRoute() {
		long[] route = new long[] { 0, 1, 2, 0 };
		long distance = RoutesOperations.getDistanceRoute(route, TestConstants.SET2_EDGES);
		assertEquals(291, distance);
	}

	@Test
	void testSplitIntoRoute() {
		long[] solution = new long[] { 0, 1, 2, 0, 3, 4, 5 };
		List<long[]> routes = RoutesOperations.splitIntoRoute(solution, 0);
		assertEquals(2, routes.size());
		assertEquals(3, routes.get(0).length);
		assertEquals(4, routes.get(1).length);
	}

	@Test
	void testGetNextDepot() {
		long[] solution = new long[] { 0, 1, 2, 0, 3, 4, 5 };
		int index = RoutesOperations.getNextDepot(solution, 1, 0);
		assertEquals(3, index);
	}

	@Test
	void testGetClientDemand() {
		long demand = RoutesOperations.getClientDemand(2, TestConstants.SET2_NODES);
		assertEquals(23, demand);
	}

	@Test
	void testGetClientDemandDataException() {
		Exception exception = assertThrows(DataException.class, () -> {
			RoutesOperations.getClientDemand(-1, TestConstants.SET2_NODES);
		});
		assertEquals(DataException.class, exception.getClass());
	}

	@Test
	void testGetRouteOverCap() {
		long[] route = new long[] { 0, 1, 2, 0 };
		long overcap = RoutesOperations.getRouteOverCap(route, TestConstants.SET2_NODES, 40);
		assertEquals(0, overcap);
	}
	
	@Test
	void testGetRouteOverCapCompl() {
		long[] route = new long[] { 0, 1, 2, 0 };
		long overcap = RoutesOperations.getRouteOverCap(route, TestConstants.SET2_NODES, 35);
		assertEquals(3, overcap);
	}
	
	@Test
	void testGetSolutionOverCap() {
		long[] solution = new long[] { 0, 1, 2, 0, 4, 5 };
		long overcap = RoutesOperations.getSolutionOverCap(solution, TestConstants.SET2_NODES, 40, 0);
		assertEquals(0, overcap);
	}
	
	@Test
	void testGetSolutionOverCapCompl() {
		long[] solution = new long[] { 0, 1, 2, 0, 3, 4 };
		long overcap = RoutesOperations.getSolutionOverCap(solution, TestConstants.SET2_NODES, 35, 0);
		assertEquals(17, overcap);
	}
}
