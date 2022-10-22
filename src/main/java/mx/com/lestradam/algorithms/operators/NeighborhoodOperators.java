package mx.com.lestradam.algorithms.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.data.DataSet;

/**
 * A neighborhood operator is used to obtain a new solution from the current solution.
 * A number of neighborhood operators (among those listed below) are chosen in advance.
 * Any combinations of the listed operators are possible, even one. 
 * @author leonardo estrada
 *
 */
@Component
public class NeighborhoodOperators {
	
	private static Random rnd = new Random();
	private long depot;
	
	@Autowired
	private DataSet dataset;
	
	@PostConstruct
	public void init() {
		depot = dataset.getDepot().getId();
	}
	
	/**
	 * Operator 1: Random swaps
	 * This operator randomly selects positions (in the solution vector) i and j with i != j and 
	 * swaps the customers located in positions i and j
	 * @param solution
	 * @return new solution
	 */
	public long[] randomSwaps(long[] solution){
		boolean right = false;
		int swapPointA;
		int swapPointB;
		int bound = solution.length;
		do{
			swapPointA = getRandomlyPoint(bound);
			swapPointB = getRandomlyPoint(bound);
			right = notEquals(swapPointA, swapPointB) && notDepot(swapPointA, swapPointB, solution, depot);
		}while(!right);
		long aux = solution[swapPointA];
		solution[swapPointA] = solution[swapPointB];
		solution[swapPointB] = aux;
		return solution;
	}
	
	/**
	 * Operator 2: Random swaps of subsequences
	 * This operator is an extension of the previous one, where two subsequences of customers and 
	 * depot of random lengths are selected and swapped
	 * @param solution
	 * @return new solution
	 */
	public long[] randomSwapsOfSubsequences(long[] solution){
		int depotA;
		int depotB; 
		int sectionStartA; 
		int sectionStartB;
		int sectionFinishA;
		int sectionFinishB; 
		int nextDepot;
		int previousDepot;
		boolean right = false;
		do{
			depotA = getRandomDepotPosition(solution, depot);
			depotB = getRandomDepotPosition(solution, depot);
			right = depotA != depotB && depotA != depotB + 1 && depotA + 1 != depotB;
		}while(!right);
		if(depotA > depotB){
			int aux = depotA;
			depotA = depotB;
			depotB = aux;
		}
		
		previousDepot = getPreviousDepot(solution, depotA, depot);		
		nextDepot = getNextDepot(solution, depotA, depot);
		
		sectionStartA = getPointInRange(previousDepot, depotA);
		sectionFinishA = getPointInRange(depotA, nextDepot);
		
		previousDepot = getPreviousDepot(solution, depotB, depot);
		nextDepot = getNextDepot(solution, depotB, depot);
		
		sectionStartB = getPointInRange(previousDepot, depotB);
		sectionFinishB = getPointInRange(depotB, nextDepot);
		
		if (sectionFinishA >= sectionStartB){
			sectionStartB = sectionFinishA + 1;
		}
		if(sectionStartA == 0){
			sectionStartB = depotB;
		}
		
		long[] sectionA = Arrays.copyOfRange(solution, 0, sectionStartA);
		long[] sectionB = Arrays.copyOfRange(solution, sectionStartA, sectionFinishA + 1);
		long[] sectionC = Arrays.copyOfRange(solution, sectionFinishA + 1, sectionStartB);
		long[] sectionD = Arrays.copyOfRange(solution, sectionStartB, sectionFinishB + 1);
		long[] sectionE = Arrays.copyOfRange(solution, sectionFinishB + 1, solution.length);
		List<long[]> sections = Arrays.asList(sectionA, sectionD, sectionC, sectionB, sectionE);
		for(long[] section : sections)
			solution = ArrayUtils.addAll(solution, section);
		return solution;
	}
	
	/**
	 * Operator 3: Random insertions
	 * This operator consists of randomly selecting positions i and j with i != j and relocating the 
	 * customer from position i to position j .
	 * @param solution
	 * @return new solution
	 */
	public long[] randomInsertions(long[] solution){
		int insertPosition;
		int insertPoint;
		int bound = solution.length;
		List<Long> tempSolution = new ArrayList<>();
		boolean right = false;
		do{
			insertPosition = getRandomlyPoint(bound);
			insertPoint = getRandomlyPoint(bound);
			right = notEquals(insertPoint, insertPosition) && notDepot(insertPoint, insertPosition, solution, depot);
		}while(!right);	
	
		for(int i = 0; i < solution.length; i++){
			if(i == insertPosition){
				tempSolution.add(solution[insertPoint]);
				tempSolution.add(solution[i]);
			}else if(i != insertPoint){
				tempSolution.add(solution[i]);
			}
		}
		solution = tempSolution.stream().mapToLong(i -> i).toArray();	
		return solution;
	}
	
	/**
	 * Operator 4: Random insertions of subsequences
	 * This operator is an extension of the operator of random insertions where a subsequence of
	 * customers and depot of random length starting from position i is relocated to position j.
	 * Positions i and j are randomly selected.
	 * @param solution
	 * @return new solution
	 */
	public long[] randomInsertionsOfSubsequences(long[] solution){
		int sectionStart;
		int sectionFinish;
		int insertPosition;
		boolean right = false;
		int bound = solution.length;
		int depotInd = getRandomDepotPosition(solution, depot);
		int previousDepot = getPreviousDepot(solution, depotInd, depot);
		int nextDepot = getNextDepot(solution, depotInd, depot);
		sectionStart = getPointInRange(previousDepot, depotInd);
		sectionFinish = getPointInRange(depotInd, nextDepot);
		
		if(sectionStart == 0){
			sectionFinish = getNextDepot(solution, sectionStart, depot) - 1;
		}
		
		do{
			insertPosition = getRandomlyPoint(bound);
			if( ( notEquals(insertPosition, sectionStart) && notEquals(insertPosition, sectionFinish) ) && notEquals(insertPosition, 0) 
					&& (insertPosition < sectionStart || insertPosition > sectionFinish) )
				right = true;
		}while(!right);
		
		long[] section = Arrays.copyOfRange(solution, sectionStart, sectionFinish + 1);
		
		List<Long> tempSolution = new ArrayList<>();
		for(int i = 0; i < solution.length ; i++){
			if(i == insertPosition){
				tempSolution.addAll(Arrays.stream(section).boxed().collect(Collectors.toList()));
				tempSolution.add(solution[i]);
			}else{
				if( (i < sectionStart) || (i > sectionFinish ) ){
					tempSolution.add(solution[i]);
				}
			}
		}

		solution = tempSolution.stream().mapToLong(i -> i).toArray();	
		return solution;		
	}
	
	private int getRandomlyPoint(int bound){
		return rnd.nextInt(bound);
	}
	
	private boolean notDepot(int pointA, int pointB, long[] solution, long depot){
		return depot != solution[pointA] && depot != solution[pointB];
	}
	
	private boolean notEquals(int pointA, int pointB){
		return pointA != pointB;	
	}
	
	private int getPreviousDepot(long[] solution, int depotPoint, long depot){
		for(int i = depotPoint - 1 ; i > 0; i-- ){
			if(solution[i] == depot)
				return i;
		}
		return 0;
	}
	
	private int getNextDepot(long[] solution, int depotPoint, long depot){
		for(int i = depotPoint + 1; i < solution.length; i++){
			if(solution[i] == depot)
				return i;
		}
		return 0;
	}
	
	private int getPointInRange(int pointA, int pointB){
		if(pointB - pointA == 0)
			return pointA;
		int rndPoint = pointA + rnd.nextInt( pointB - pointA );
		if (rndPoint == pointA)
			rndPoint++;
		return rndPoint;
	}
	
	private int getRandomDepotPosition(long[] solution, long depotId){
		int depot = -1;
		boolean right = false;
		int bound = solution.length;
		do{
			depot = getRandomlyPoint(bound);
			right = solution[depot] == depotId;
		}while(!right);
		return depot;
	}

}
