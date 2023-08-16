package mx.com.lestradam.algorithms.functions.basic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BasicOperationsTest {

	@Test
	void testGetMaxValueIndexDouble() {
		double[] array = { 1, 2, 3, 0, 1, 8, 7 };
		int expectedIndex = 5;
		int actualindex = BasicOperations.getMaxValueIndex(array);
		assertEquals(expectedIndex, actualindex);
	}

	@Test
	void testGetMaxValueIndexLong() {
		long[] array = { 1, 2, 3, 0, 1, 8, 7 };
		int expectedIndex = 5;
		int actualindex = BasicOperations.getMaxValueIndex(array);
		assertEquals(expectedIndex, actualindex);
	}

	@Test
	void testGetMinValueIndexDouble() {
		double[] array = { 1, 2, 3, 0, 1, 8, 7 };
		int expectedIndex = 3;
		int actualindex = BasicOperations.getMinValueIndex(array);
		assertEquals(expectedIndex, actualindex);
	}

	@Test
	void testGetMinValueIndexLong() {
		long[] array = { 1, 2, 3, 0, 1, 8, 7 };
		int expectedIndex = 3;
		int actualindex = BasicOperations.getMinValueIndex(array);
		assertEquals(expectedIndex, actualindex);
	}

	@Test
	void testGetRandomlyPoint() {
		int bound = 10;
		int actualPoint = BasicOperations.getRandomlyPoint(bound);
		assertTrue(actualPoint < 10 && actualPoint >= 0);
	}
	
	@Test
	void testGetNthMinValueIndex() {
		long[] unOrderedArray = new long[] {2,3,1,4,5};
		int nElement = 0;
		int nthMin = BasicOperations.getNthMinValueIndex(unOrderedArray, nElement);
		assertEquals(2, nthMin);
	}

}
