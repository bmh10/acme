package com.acmetelecom;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

import org.joda.time.DateTime;

/**
 * High-level billing system logic, concerned with storing call events and creating customer bills.
 */
public class BillingSystem implements IBillingSystem {

	static Logger log = Logger.getLogger(BillingSystem.class.getSimpleName());
	
	// TODO: DONE change this to hash map key => caller
	// The call event log which is indexed by caller phone number.
    private HashMap<String, ArrayList<CallEvent>> callLog = new HashMap<String, ArrayList<CallEvent>>();
 
    private ICallCostCalculator callCostCalculator;
    private IBillGenerator billGenerator;
    private CustomerDatabase customerDatabase;
    
    /**
     * Constructor.
     * @param callCostCalculator The call cost calculator to use when generating bills.
     * @param billGenerator The bill generator to use to generate bills.
     * @param customerDatabase The customer database to refer to for customer information.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public BillingSystem(ICallCostCalculator callCostCalculator, IBillGenerator billGenerator, CustomerDatabase customerDatabase) {
    	// TODO: are we allowed to change constructor of BillingSystem? If not then need to change this -> use a factory class.
    	AssertionHelper.NotNull(callCostCalculator, "callCostCalculator");
    	AssertionHelper.NotNull(billGenerator, "billGenerator");
    	AssertionHelper.NotNull(customerDatabase, "customerDatabase");
    	this.callCostCalculator = callCostCalculator;
    	this.billGenerator = billGenerator;
    	this.customerDatabase = customerDatabase;
    	MoneyFormatter.penceToPounds(null);
    }

    /**
     * Called when a call is started.
     * @param caller The caller phone number.
     * @param callee The callee phone number.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public void callInitiated(String caller, String callee) {
    	AssertionHelper.NotNull(caller, "caller");
    	AssertionHelper.NotNull(callee, "callee");
    	log.info("Call started: from '" + caller + "' to '" + callee + "' at " + DateTime.now().toString());
    	addEventToLog(caller, new CallStart(caller, callee, DateTime.now()));
    }

    /**
     * Called when a call is ended.
     * @param caller The caller phone number.
     * @param callee The callee phone number.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public void callCompleted(String caller, String callee) {
    	AssertionHelper.NotNull(caller, "caller");
    	AssertionHelper.NotNull(callee, "callee");
    	log.info("Call ended: from '" + caller + "' to '" + callee + "' at " + DateTime.now().toString());
    	addEventToLog(caller, new CallEnd(caller, callee, DateTime.now()));
    }
    
    // TODO: better way to do this.
//    public void callInitiatedAtTime(String caller, String callee, DateTime time) {
//    	addEventToLog(caller, new CallStart(caller, callee, time));
//    }
//
//    public void callCompletedAtTime(String caller, String callee, DateTime time) {
//    	addEventToLog(caller, new CallEnd(caller, callee, time));
//    }

    /**
     * Creates bills for all customers, prints them out and returns them as a list of type Bill.
     * @return ArrayList<Bill> The list of created bills, one per customer.
     */
    public ArrayList<Bill> createCustomerBills() {
        List<Customer> customers = customerDatabase.getCustomers();
        ArrayList<Bill> customerBills = new ArrayList<Bill>();
        
        for (Customer customer : customers) {
            Bill bill = createBillFor(customer);
            customerBills.add(bill);
        }
        
        callLog.clear();
        return customerBills;
    }

    /**
     * Creates a bill for a specific customer.
     * @param customer The customer to create a bill for.
     * @return Bill The customer's bill.
     */
    private Bill createBillFor(Customer customer) {
        List<CallEvent> customerEvents = callLog.get(customer.getPhoneNumber());
        if (customerEvents == null) {
        	return new Bill(customer, new ArrayList<LineItem>(), MoneyFormatter.penceToPounds(new BigDecimal(0.0)));
        }
        
        // Separate events into specific calls.
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

        // TODO: DONE move this into separate class, billing system shouldn't be concerned with individual call costing...
        for (Call call : calls) {
        	BigDecimal callCost = callCostCalculator.calculateCallCost(customer, call);
            totalBill = totalBill.add(callCost);
            items.add(new LineItem(call, callCost));
        }

        Bill bill = new Bill(customer, items, MoneyFormatter.penceToPounds(totalBill));
        billGenerator.generateBill(bill);
        return bill;
    }
    
    /**
     * Adds an event to the event log indexed by phone number.
     * @param caller The phone number of the caller.
     * @param event The event to add to the event log.
     */
    private void addEventToLog(String caller, CallEvent event) {
    	ArrayList<CallEvent> callEvents = callLog.get(caller);
    	if (callEvents == null) {
    		callEvents = new ArrayList<CallEvent>();
    	}
    	
    	callEvents.add(event);
        callLog.put(caller, callEvents);
    }
}
