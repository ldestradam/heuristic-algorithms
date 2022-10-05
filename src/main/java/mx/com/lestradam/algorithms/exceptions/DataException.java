package mx.com.lestradam.algorithms.exceptions;

public class DataException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public DataException(String message) {
		super(message);
	}
	
	public DataException(String message, Throwable ex) {
		super(message, ex);
	}

}
