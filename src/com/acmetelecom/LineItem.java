package com.acmetelecom;

import java.math.BigDecimal;

// TODO: why was this previously static and put in same file as BillingSystem?
/**
 * Represents an call item to be put in a customer's bill.
 */
public class LineItem {
    private Call call;
    private BigDecimal callCost;

    /**
     * Constructor.
     * @param call The call.
     * @param callCost The cost of the call.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public LineItem(Call call, BigDecimal callCost) {
    	AssertionHelper.NotNull(call, "call");
    	AssertionHelper.NotNull(callCost, "callCost");
        this.call = call;
        this.callCost = callCost;
    }

    /**
     * Gets the date the call was started on.
     * @return The date the call was started on as a String.
     */
    public String date() {
        return call.date();
    }

    /**
     * Gets the callee associated with this call item.
     * @return The callee's phone number.
     */
    public String callee() {
        return call.callee();
    }

    /**
     * Gets the call duration in minutes and seconds as a formatted String.
     * @return The call duration as a formatted String.
     */
    public String durationMinutes() {
        return "" + call.durationSeconds() / 60 + ":" + String.format("%02d", call.durationSeconds() % 60);
    }

    /**
     * Gets the call cost.
     * @return The call cost.
     */
    public BigDecimal cost() {
        return callCost;
    }
}
