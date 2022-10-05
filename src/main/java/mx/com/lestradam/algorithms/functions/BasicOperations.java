package mx.com.lestradam.algorithms.functions;

import java.util.Random;

public class BasicOperations {
	
	private static Random rnd = new Random();
	
	private BasicOperations() {
		throw new IllegalStateException("Utility class");
	}
	
	public static int getMinimunCostIndex(long[] array) {
		int index = 0;
		long minimun = array[0];
		for(int i = 0; i< array.length; i++){
			if(array[i] < minimun){
				index = i;
				minimun = array[i]; 
			}
		}
		return index;
	}
	
	public static int getNextDepot(long[] solution, int offset, long depot){
		for(int i = offset + 1; i < solution.length; i++){
			if(solution[i] == depot)
				return i;
		}
		return 0;
	}
	
	public static int getRandomlyPoint(int bound){
		return rnd.nextInt(bound);
	}

}
