package com.acmetelecom;

import java.util.ArrayList;

import org.joda.time.DateTime;

/**
 * The billing system interface which all billing systems should implement.
 */
public interface IBillingSystem {
	
	/**
     * Called when a call is started.
     * @param caller The caller phone number.
     * @param callee The callee phone number.
     */
	void callInitiated(String caller, String callee);

	/**
     * Called when a call is ended.
     * @param caller The caller phone number.
     * @param callee The callee phone number.
     */
    void callCompleted(String caller, String callee);
    
//    void callInitiatedAtTime(String caller, String callee, DateTime time);
//    	
//    void callCompletedAtTime(String caller, String callee, DateTime time);

    /**
     * Creates bills for all customers, prints them out and returns them as a list of type Bill.
     * @return ArrayList<Bill> The list of created bills, one per customer.
     */
    ArrayList<Bill> createCustomerBills();
}
