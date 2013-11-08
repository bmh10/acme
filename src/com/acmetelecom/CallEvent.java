package com.acmetelecom;

/**
 * Contains information about a call event.
 */
public abstract class CallEvent {
    private String caller;
    private String callee;
    private long time;

    /**
     * Constructor.
     * @param caller The caller's phone number.
     * @param callee The callee's phone number.
     * @param timeStamp The time this event occurred (in milliseconds since epoch).
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public CallEvent(String caller, String callee, long timeStamp) {
    	AssertionHelper.NotNull(caller, "caller");
    	AssertionHelper.NotNull(callee, "callee");
        this.caller = caller;
        this.callee = callee;
        this.time = timeStamp;
    }

    /**
     * Gets the caller's phone number.
     * @return The caller's phone number.
     */
    public String getCaller() {
        return caller;
    }

    /**
     * Gets the callee's phone number.
     * @return The callee's phone number.
     */
    public String getCallee() {
        return callee;
    }

    /**
     * Gets the time this event occurred.
     * @return The time this event occurred (in milliseconds since epoch)
     */
    public long time() {
        return time;
    }
}
