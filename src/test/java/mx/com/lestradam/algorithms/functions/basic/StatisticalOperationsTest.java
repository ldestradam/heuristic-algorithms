package mx.com.lestradam.algorithms.functions.basic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import mx.com.lestradam.algorithms.functions.basic.StatisticalOperations;

class StatisticalOperationsTest {

	@Test
	void testGetAvgValue() {
		long[] values = new long[] {100,100,300, 102};
		double avg = StatisticalOperations.getAvgValue(values);
		assertEquals(150.5, avg);
	}

}
