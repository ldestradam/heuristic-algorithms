package mx.com.lestradam.algorithms.exceptions;

public class AlgorithmExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlgorithmExecutionException(String message) {
		super(message);
	}

	public AlgorithmExecutionException(String message, Throwable ex) {
		super(message, ex);
	}

}
