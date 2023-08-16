package mx.com.lestradam.algorithms.functions.basic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StatisticalOperationsTest {

	@Test
	void testGetAvgValue() {
		double[] values = new double[] { 100, 100, 300, 102 };
		double avg = StatisticalOperations.getAvgValue(values);
		assertEquals(150.5, avg);
	}

}
