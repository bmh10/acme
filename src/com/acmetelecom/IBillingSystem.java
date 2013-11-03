package com.acmetelecom;

public interface IBillingSystem {
	
	void callInitiated(String caller, String callee);

    void callCompleted(String caller, String callee);

    void createCustomerBills();
}
