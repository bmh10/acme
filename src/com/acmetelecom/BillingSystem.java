package com.acmetelecom;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

import org.joda.time.DateTime;

public class BillingSystem implements IBillingSystem {

	// TODO: change this to hash map key => caller
    private HashMap<String, ArrayList<CallEvent>> callLog = new HashMap<String, ArrayList<CallEvent>>();
    
    private ICallCostCalculator callCostCalculator;
    
    private IBillGenerator billGenerator;
    
    CustomerDatabase customerDatabase;
    
    static Logger log = Logger.getLogger(BillingSystem.class.getSimpleName());
    
    public BillingSystem(ICallCostCalculator callCostCalculator, IBillGenerator billGenerator, CustomerDatabase customerDatabase) {
    	// TODO: are we allowed to change constructor of BillingSystem? If so then pass in CallCostCalculator object.
//    	this.callCostCalculator = new CallCostCalculator();
//    	HtmlPrinter htmlPrinter = new HtmlPrinter();
//    	this.billGenerator = new HtmlBillGenerator(htmlPrinter);
//    	this.customerDatabase = CentralCustomerDatabase.getInstance();
    	this.callCostCalculator = callCostCalculator;
    	this.billGenerator = billGenerator;
    	this.customerDatabase = customerDatabase;
    }

    public void callInitiated(String caller, String callee) {
    	log.info("Call started - from '" + caller + "' to '" + callee + "' at " + DateTime.now().toString());
    	addEventToLog(caller, new CallStart(caller, callee, DateTime.now()));
    }

    public void callCompleted(String caller, String callee) {
    	log.info("Call ended - from '" + caller + "' to '" + callee + "' at " + DateTime.now().toString());
    	addEventToLog(caller, new CallEnd(caller, callee, DateTime.now()));
    }
    
    // TODO: better way to do this.
    public void callInitiatedAtTime(String caller, String callee, DateTime time) {
    	addEventToLog(caller, new CallStart(caller, callee, time));
    }

    public void callCompletedAtTime(String caller, String callee, DateTime time) {
    	addEventToLog(caller, new CallEnd(caller, callee, time));
    }

    public ArrayList<Bill> createCustomerBills() {
        List<Customer> customers = customerDatabase.getCustomers();
        ArrayList<Bill> customerBills = new ArrayList<Bill>();
        
        for (Customer customer : customers) {
        	//System.out.println("Name: " + customer.getFullName() + " No: " + customer.getPhoneNumber() + "Plan: " + customer.getPricePlan());
            Bill bill = createBillFor(customer);
            if (bill != null) {
            	customerBills.add(bill);
            }
        }
        
        callLog.clear();
        return customerBills;
    }

    private Bill createBillFor(Customer customer) {
        List<CallEvent> customerEvents = callLog.get(customer.getPhoneNumber());
        if (customerEvents == null) {
        	return new Bill(customer, new ArrayList<LineItem>(), MoneyFormatter.penceToPounds(new BigDecimal(0.0)));
        }
        
//        for (CallEvent callEvent : callLog) {
//            if (callEvent.getCaller().equals(customer.getPhoneNumber())) {
//                customerEvents.add(callEvent);
//            }
//        }

        List<Call> calls = new ArrayList<Call>();

        CallEvent start = null;
        // TODO: this assumes customer events are in order...change it to check start 
        // TODO: order events by time in pairs?
        for (CallEvent event : customerEvents) {
            if (event instanceof CallStart) {
                start = event;
            }
            if (event instanceof CallEnd && start != null 
            	&& start.getCaller() == event.getCaller() && start.getCallee() == event.getCallee()) {
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

        Bill bill = new Bill(customer, items, MoneyFormatter.penceToPounds(totalBill));
        billGenerator.generateBill(bill);
        return bill;
    }
    
    private void addEventToLog(String caller, CallEvent event) {
    	ArrayList<CallEvent> callEvents = callLog.get(caller);
    	if (callEvents == null) {
    		callEvents = new ArrayList<CallEvent>();
    	}
    	
    	callEvents.add(event);
        callLog.put(caller, callEvents);
    }
}
