package mx.com.lestradam.algorithms.functions.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicOperations {

	private static Logger logger = LoggerFactory.getLogger(BasicOperations.class);
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

	public static double[] generateRandomArrayNumbers(final int size) {
		double[] randomNumbers = new double[size];
		for (int i = 0; i < size; i++)
			randomNumbers[i] = Math.random();
		return randomNumbers;
	}

	public static int findNthSmallestIndex(final double[] arr, final int nth) {
		if (arr == null || nth < 0 || nth >= arr.length)
			throw new IllegalArgumentException("Invalid input");
		Map<Double, List<Integer>> indexMap = new HashMap<>();
		for (int i = 0; i < arr.length; i++) {
			indexMap.computeIfAbsent(arr[i], key -> new ArrayList<>()).add(i);
		}
		int count = 0;
		SortedSet<Double> keys = new TreeSet<>(indexMap.keySet());
		for (Double key : keys) {
			List<Integer> indices = indexMap.get(key);
			if (logger.isTraceEnabled())
				logger.trace("Key:{} Value: {}", key, Arrays.toString(indices.toArray()));
			count += indices.size();
			if (count > nth) {
				int index = nth - (count - indices.size());
				if (logger.isTraceEnabled()) {
					logger.trace("Nth: {} Index:{} Value: {} ", nth, index, indices.get(index));
				}
				return indices.get(index);
			}
		}
		return -1; // Element not found (should not reach here)
	}

}
