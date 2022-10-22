package mx.com.lestradam.algorithms.functions.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	
	public static List<long[]> splitIntoRoute(long[] chromosome, long depot){
		List<long[]> routes = new ArrayList<>();
		int offset = 0;
		int depotInd = getNextDepot(chromosome, offset, depot);
		do {
			routes.add(Arrays.copyOfRange(chromosome, offset, depotInd));			
			offset = depotInd;
			depotInd = getNextDepot(chromosome, offset, depot);
			if(depotInd == 0)
				routes.add(Arrays.copyOfRange(chromosome, offset, chromosome.length));
		} while (depotInd != 0);
		return routes;
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
