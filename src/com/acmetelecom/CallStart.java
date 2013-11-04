package com.acmetelecom;

import org.joda.time.DateTime;

public class CallStart extends CallEvent {
    public CallStart(String caller, String callee, DateTime time) {
        super(caller, callee, time.getMillis());
    }
}
