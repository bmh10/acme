package com.acmetelecom;

import java.util.logging.Logger;

public class AssertionHelper {

	private static Logger log = Logger.getLogger(AssertionHelper.class.getSimpleName());
	
	public static void NotNull(Object o, String paramName) {
		if (o == null) {
			throw new IllegalArgumentException(paramName + " cannot be null.");
		}
	}
}
