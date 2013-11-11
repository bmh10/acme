package com.acmetelecom;

import org.joda.time.DateTime;

/**
 * Basic implementation of a standard clock.
 */
public class Clock implements IClock {

	/**
	 * Gets the current time.
	 * @return The current time as a DateTime object.
	 */
	@Override
	public DateTime now() {
		return DateTime.now();
	}
}
