package mx.com.lestradam.algorithms.functions.fitness;

public interface PenaltyFunction {
	
	public double evaluate(long[] solution);
	public double excess(long[] solution);

}
