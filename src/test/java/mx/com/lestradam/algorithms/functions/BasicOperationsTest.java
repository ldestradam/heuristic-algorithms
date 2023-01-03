package mx.com.lestradam.algorithms.functions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import mx.com.lestradam.algorithms.functions.basic.BasicOperations;

class BasicOperationsTest {

	@Test
	void testGetMaxValueIndex() {
		long[] array = {1,2,3,0,1,8,7};
		int expectedIndex = 5;
		int actualindex = BasicOperations.getMaxValueIndex(array);
		assertEquals(expectedIndex, actualindex);
	}

	@Test
	void testGetMinValueIndex() {
		long[] array = {1,2,3,0,1,8,7};
		int expectedIndex = 3;
		int actualindex = BasicOperations.getMinValueIndex(array);
		assertEquals(expectedIndex, actualindex);
	}

	@Test
	void testGetRandomlyPoint() {
		int bound = 10;
		int actualPoint = BasicOperations.getRandomlyPoint(bound);
		assertTrue( actualPoint < 10 && actualPoint >= 0);
	}

}
