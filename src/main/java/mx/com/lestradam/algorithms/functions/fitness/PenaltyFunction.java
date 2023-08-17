package mx.com.lestradam.algorithms.functions.fitness;

public interface PenaltyFunction {
	
	public double evaluate(long[] solution);
	public long excess(long[] solution);

}
