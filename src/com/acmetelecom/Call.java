package com.acmetelecom;

import java.text.SimpleDateFormat;
import org.joda.time.*;

public class Call {
    private CallEvent start;
    private CallEvent end;

    public Call(CallEvent start, CallEvent end) {
        this.start = start;
        this.end = end;
    }

    public String callee() {
        return start.getCallee();
    }

    public int durationSeconds() {
        return (int) (((end.time() - start.time()) / 1000));
    }

    public String date() {
        //return SimpleDateFormat.getInstance().format(new Date(start.time()));
    	// TODO: make new formatter.
        return "TODO date formatter"; //(new DateTime(start.time()));
    }

    public DateTime startTime() {
        return new DateTime(start.time());
    }

    public DateTime endTime() {
        return new DateTime(end.time());
    }
}
