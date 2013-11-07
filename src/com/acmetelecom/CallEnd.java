package com.acmetelecom;

import org.joda.time.DateTime;

/**
 * Represents a call end event.
 */
public class CallEnd extends CallEvent {
	
	/**
	 * Constructor.
	 * @param caller The caller's phone number.
	 * @param callee The callee's phone number.
	 * @param time The time the event occurred (in milliseconds since the epoch).
	 */
    public CallEnd(String caller, String callee, DateTime time) {
        super(caller, callee, time.getMillis());
    }
}
