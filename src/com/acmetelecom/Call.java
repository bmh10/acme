package com.acmetelecom;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Contains information about a specific call.
 */
public class Call {
    private CallEvent start;
    private CallEvent end;

    /**
     * Constructor.
     * @param start The start of call event.
     * @param end The end of call event.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public Call(CallEvent start, CallEvent end) {
    	AssertionHelper.NotNull(start, "start");
    	AssertionHelper.NotNull(end, "end");
        this.start = start;
        this.end = end;
    }

    /**
     * Gets the callee associated with this call.
     * @return The callee's phone number.
     */
    public String callee() {
        return start.getCallee();
    }

    /**
     * Gets the call duration in seconds.
     * @return The call duration in seconds.
     */
    public int durationSeconds() {
        return (int) (((end.time() - start.time()) / 1000));
    }

    /**
     * Get the date the call was started on.
     * @return The date the call was started on as a String.
     */
    public String date() {
    	DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    	return dateFormatter.print(start.time());
    }

    /**
     * Gets the call start time.
     * @return The call start time.
     */
    public DateTime startTime() {
        return new DateTime(start.time());
    }

    /**
     * Gets the call end time.
     * @return The call end time.
     */
    public DateTime endTime() {
        return new DateTime(end.time());
    }
}
