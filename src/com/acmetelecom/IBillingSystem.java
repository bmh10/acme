package com.acmetelecom;

import java.util.ArrayList;

import org.joda.time.DateTime;

public interface IBillingSystem {
	
	void callInitiated(String caller, String callee);

    void callCompleted(String caller, String callee);
    
//    void callInitiatedAtTime(String caller, String callee, DateTime time);
//    	
//    void callCompletedAtTime(String caller, String callee, DateTime time);

    ArrayList<Bill> createCustomerBills();
}
