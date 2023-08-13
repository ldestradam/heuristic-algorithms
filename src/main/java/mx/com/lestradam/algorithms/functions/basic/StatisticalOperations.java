package mx.com.lestradam.algorithms.functions.basic;

public class StatisticalOperations {
	
	private StatisticalOperations() {
		throw new IllegalStateException("Utility class");
	}
	
	public static double getAvgValue(long[] values) {
		long sum = 0;
		for(int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return (double) sum / values.length;
	}
	
}
