package com.acmetelecom;

import org.joda.time.DateTime;

public class CallEnd extends CallEvent {
    public CallEnd(String caller, String callee, DateTime time) {
        super(caller, callee, time.getMillis());
    }
}
