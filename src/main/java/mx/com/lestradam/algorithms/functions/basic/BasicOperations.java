package mx.com.lestradam.algorithms.functions.basic;

import java.util.Random;

public class BasicOperations {
	
	private static Random rnd = new Random();
	
	private BasicOperations() {
		throw new IllegalStateException("Utility class");
	}
	
	public static int getMaxValueIndex(long[] array) {
		int index = 0;
		long max = array[0];
		for(int i = 1; i< array.length; i++){
			if(array[i] > max){
				index = i;
				max = array[i]; 
			}
		}
		return index;
	}
	
	public static int getMinValueIndex(long[] array) {
		int index = 0;
		long minimun = array[0];
		for(int i = 1; i< array.length; i++){
			if(array[i] < minimun){
				index = i;
				minimun = array[i]; 
			}
		}
		return index;
	}
	
	public static int getRandomlyPoint(int bound){
		return rnd.nextInt(bound);
	}

}
