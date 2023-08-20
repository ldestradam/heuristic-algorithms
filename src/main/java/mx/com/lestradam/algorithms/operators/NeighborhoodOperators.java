package mx.com.lestradam.algorithms.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.com.lestradam.algorithms.elements.DataSet;

/**
 * A neighborhood operator is used to obtain a new solution from the current solution.
 * A number of neighborhood operators (among those listed below) are chosen in advance.
 * Any combinations of the listed operators are possible, even one. 
 * @author leonardo estrada
 *
 */
@Component
public class NeighborhoodOperators {
	
	private static Logger logger = LoggerFactory.getLogger(NeighborhoodOperators.class);
	
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
	public long[] randomSwaps(final long[] solution){
		if (logger.isTraceEnabled()) {
			logger.trace("Random swap");
			logger.trace("Solution: {}", Arrays.toString(solution));
		}
		boolean right = false;
		int swapPointA;
		int swapPointB;
		int bound = solution.length;
		long[] tmpSolution = Arrays.copyOf(solution, solution.length);
		do{
			swapPointA = getRandomlyPoint(bound);
			swapPointB = getRandomlyPoint(bound);
			right = notEquals(swapPointA, swapPointB) && notDepot(swapPointA, swapPointB, tmpSolution);
		}while(!right);
		logger.trace("Point a: {} - Point b: {}", swapPointA, swapPointB);
		long aux = tmpSolution[swapPointA];
		tmpSolution[swapPointA] = tmpSolution[swapPointB];
		tmpSolution[swapPointB] = aux;
		if (logger.isTraceEnabled()) {
			logger.trace("Solution mutated: {}", Arrays.toString(tmpSolution));
		}
		return tmpSolution;
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
		long[] tmpSolution = Arrays.copyOf(solution, solution.length);
		do{
			depotA = getRandomDepotPosition(tmpSolution);
			depotB = getRandomDepotPosition(tmpSolution);
			right = depotA != depotB && depotA != depotB + 1 && depotA + 1 != depotB;
		}while(!right);
		if(depotA > depotB){
			int aux = depotA;
			depotA = depotB;
			depotB = aux;
		}
		
		previousDepot = getPreviousDepot(tmpSolution, depotA, depot);		
		nextDepot = getNextDepot(tmpSolution, depotA, depot);
		
		sectionStartA = getPointInRange(previousDepot, depotA);
		sectionFinishA = getPointInRange(depotA, nextDepot);
		
		previousDepot = getPreviousDepot(tmpSolution, depotB, depot);
		nextDepot = getNextDepot(tmpSolution, depotB, depot);
		
		sectionStartB = getPointInRange(previousDepot, depotB);
		sectionFinishB = getPointInRange(depotB, nextDepot);
		
		if (sectionFinishA >= sectionStartB){
			sectionStartB = sectionFinishA + 1;
		}
		if(sectionStartA == 0){
			sectionStartB = depotB;
		}
		
		long[] sectionA = Arrays.copyOfRange(tmpSolution, 0, sectionStartA);
		long[] sectionB = Arrays.copyOfRange(tmpSolution, sectionStartA, sectionFinishA + 1);
		long[] sectionC = Arrays.copyOfRange(tmpSolution, sectionFinishA + 1, sectionStartB);
		long[] sectionD = Arrays.copyOfRange(tmpSolution, sectionStartB, sectionFinishB + 1);
		long[] sectionE = Arrays.copyOfRange(tmpSolution, sectionFinishB + 1, tmpSolution.length);
		List<long[]> sections = Arrays.asList(sectionA, sectionD, sectionC, sectionB, sectionE);
		for(long[] section : sections)
			tmpSolution = ArrayUtils.addAll(tmpSolution, section);
		return tmpSolution;
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
		long[] randSolution = Arrays.copyOf(solution, solution.length);
		boolean right = false;
		do{
			insertPosition = getRandomlyPoint(bound);
			insertPoint = getRandomlyPoint(bound);
			right = notEquals(insertPoint, insertPosition) && notDepot(insertPoint, insertPosition, randSolution);
		}while(!right);	
	
		for(int i = 0; i < randSolution.length; i++){
			if(i == insertPosition){
				tempSolution.add(randSolution[insertPoint]);
				tempSolution.add(randSolution[i]);
			}else if(i != insertPoint){
				tempSolution.add(randSolution[i]);
			}
		}
		randSolution = tempSolution.stream().mapToLong(i -> i).toArray();	
		return randSolution;
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
		long[] randSolution = Arrays.copyOf(solution, solution.length);
		int depotInd = getRandomDepotPosition(randSolution);
		int previousDepot = getPreviousDepot(randSolution, depotInd, depot);
		int nextDepot = getNextDepot(randSolution, depotInd, depot);
		sectionStart = getPointInRange(previousDepot, depotInd);
		sectionFinish = getPointInRange(depotInd, nextDepot);
		if(sectionStart == 0){
			sectionFinish = getNextDepot(randSolution, sectionStart, depot) - 1;
		}
		do{
			insertPosition = getRandomlyPoint(bound);
			if( ( notEquals(insertPosition, sectionStart) && notEquals(insertPosition, sectionFinish) ) && notEquals(insertPosition, 0) 
					&& (insertPosition < sectionStart || insertPosition > sectionFinish) )
				right = true;
		}while(!right);
		long[] section = Arrays.copyOfRange(randSolution, sectionStart, sectionFinish + 1);
		List<Long> tempSolution = new ArrayList<>();
		for(int i = 0; i < randSolution.length ; i++){
			if(i == insertPosition){
				tempSolution.addAll(Arrays.stream(section).boxed().collect(Collectors.toList()));
				tempSolution.add(randSolution[i]);
			}else{
				if( (i < sectionStart) || (i > sectionFinish ) ){
					tempSolution.add(randSolution[i]);
				}
			}
		}
		randSolution = tempSolution.stream().mapToLong(i -> i).toArray();	
		return randSolution;
	}
	
	private int getRandomlyPoint(int bound){
		return rnd.nextInt(bound);
	}
	
	private boolean notDepot(int pointA, int pointB, long[] solution){
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
	
	private int getRandomDepotPosition(long[] solution){
		int depotId = -1;
		boolean right = false;
		int bound = solution.length;
		do{
			depotId = getRandomlyPoint(bound);
			right = solution[depotId] == depot;
		}while(!right);
		return depotId;
	}

}
