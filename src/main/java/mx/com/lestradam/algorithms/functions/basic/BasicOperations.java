package mx.com.lestradam.algorithms.functions.basic;

import java.util.Arrays;
import java.util.Random;

public class BasicOperations {

	private static Random rnd = new Random();

	private BasicOperations() {
		throw new IllegalStateException("Utility class");
	}

	public static int getMaxValueIndex(final double[] array) {
		int index = 0;
		double max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				index = i;
				max = array[i];
			}
		}
		return index;
	}

	public static int getMinValueIndex(final double[] array) {
		int index = 0;
		double minimun = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < minimun) {
				index = i;
				minimun = array[i];
			}
		}
		return index;
	}

	public static int getMaxValueIndex(final long[] array) {
		int index = 0;
		long max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				index = i;
				max = array[i];
			}
		}
		return index;
	}

	public static int getMinValueIndex(final long[] array) {
		int index = 0;
		long minimun = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] < minimun) {
				index = i;
				minimun = array[i];
			}
		}
		return index;
	}

	public static int getNthMinValueIndex(final long[] unOrderedArray, final int nElement) {
		long[] values = Arrays.copyOf(unOrderedArray, unOrderedArray.length);
		Arrays.sort(values);
		long value = values[nElement];
		int position = 0;
		for (int j = 0; j < values.length; j++) {
			if (value == unOrderedArray[j]) {
				position = j;
				break;
			}
		}
		return position;
	}
	
	public static int getNthMinValueIndex(final double[] unOrderedArray, final int nElement) {
		double[] values = Arrays.copyOf(unOrderedArray, unOrderedArray.length);
		Arrays.sort(values);
		double value = values[nElement];
		int position = 0;
		for (int j = 0; j < values.length; j++) {
			if (value == unOrderedArray[j]) {
				position = j;
				break;
			}
		}
		return position;
	}

	public static int getRandomlyPoint(int bound) {
		return rnd.nextInt(bound);
	}

}
