package com.acmetelecom;

import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BillingSystem implements IBillingSystem {

	// TODO: change this to hash map key => caller
    private HashMap<String, ArrayList<CallEvent>> callLog = new HashMap<String, ArrayList<CallEvent>>();
    
    private ICallCostCalculator callCostCalculator;
    
    public BillingSystem() {
    	// TODO: are we allowed to change constructor of BillingSystem? If so then pass in CallCostCalculator object.
    	this.callCostCalculator = new CallCostCalculator();
    }

    public void callInitiated(String caller, String callee) {
    	ArrayList<CallEvent> callEvents = callLog.get(caller);
    	if (callEvents == null) {
    		callEvents = new ArrayList<CallEvent>();
    	}
    	
    	callEvents.add(new CallStart(caller, callee));
        callLog.put(caller, callEvents);
    }

    public void callCompleted(String caller, String callee) {
    	ArrayList<CallEvent> callEvents = callLog.get(caller);
    	if (callEvents == null) {
    		callEvents = new ArrayList<CallEvent>();
    	}
    	
    	callEvents.add(new CallEnd(caller, callee));
        callLog.put(caller, callEvents);
    }

    public void createCustomerBills() {
        List<Customer> customers = CentralCustomerDatabase.getInstance().getCustomers();
        for (Customer customer : customers) {
        	System.out.println("Name: " + customer.getFullName() + " No: " + customer.getPhoneNumber() + "Plan: " + customer.getPricePlan());
            createBillFor(customer);
        }
        callLog.clear();
    }

    private void createBillFor(Customer customer) {
        List<CallEvent> customerEvents = callLog.get(customer.getPhoneNumber());
        if (customerEvents == null) {
        	return;
        }
        
//        for (CallEvent callEvent : callLog) {
//            if (callEvent.getCaller().equals(customer.getPhoneNumber())) {
//                customerEvents.add(callEvent);
//            }
//        }

        List<Call> calls = new ArrayList<Call>();

        CallEvent start = null;
        // TODO: this assumes customer events are in order...change it
        for (CallEvent event : customerEvents) {
            if (event instanceof CallStart) {
                start = event;
            }
            if (event instanceof CallEnd && start != null) {
                calls.add(new Call(start, event));
                start = null;
            }
        }

        BigDecimal totalBill = new BigDecimal(0);
        List<LineItem> items = new ArrayList<LineItem>();

        // TODO: move this into separate class, billing system shouldn't be concerned with individual call costing...
        for (Call call : calls) {
        	BigDecimal callCost = callCostCalculator.calculateCallCost(customer, call);
            totalBill = totalBill.add(callCost);
            items.add(new LineItem(call, callCost));
        }

        new BillGenerator().send(customer, items, MoneyFormatter.penceToPounds(totalBill));
    }
}
