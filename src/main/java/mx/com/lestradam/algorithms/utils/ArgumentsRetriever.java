package mx.com.lestradam.algorithms.utils;

import mx.com.lestradam.algorithms.exceptions.DataException;

public class ArgumentsRetriever {
	
	private ArgumentsRetriever() {
	    throw new IllegalStateException("Utility class");
	  }
	
	private static final String SEPARATOR = ":";
	
	public static boolean checkArgumentKey(final String[] arguments, final String key) {
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].indexOf(key) == 0)
				return true;
		}
		return false;
	}

	public static String retrieveArgumentValue(final String[] arguments, final String key) {
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].indexOf(key) == 0) {
				String arg = arguments[i];
				return arg.substring(arg.indexOf(SEPARATOR) + 1, arg.length());
			}
		}
		throw new DataException("Missing value for parameter: " + key);
	}

}
