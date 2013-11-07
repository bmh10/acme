package com.acmetelecom;

/**
 * Utility class to help with making general assertions.
 */
public class AssertionHelper {
	
	/**
	 * Makes an object not null assertion.
	 * @param o The object to check.
	 * @param paramName The object name.
	 * @exception IllegalArgumentException Thrown if the object is null.
	 */
	public static void NotNull(Object o, String paramName) {
		if (o == null) {
			throw new IllegalArgumentException(paramName + " cannot be null.");
		}
	}
}
