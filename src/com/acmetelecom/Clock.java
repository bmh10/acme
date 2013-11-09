package com.acmetelecom;

import org.joda.time.DateTime;

public class Clock implements IClock {

	@Override
	public DateTime now() {
		return DateTime.now();
	}

}
