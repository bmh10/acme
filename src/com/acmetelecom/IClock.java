package com.acmetelecom;

import org.joda.time.DateTime;

/**
 * The clock interface which all clocks should implement.
 */
public interface IClock {

	/**
	 * Gets the current time.
	 * @return The current time as a DateTime object.
	 */
	public DateTime now();
}
