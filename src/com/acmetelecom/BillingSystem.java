package com.acmetelecom;

import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.TariffLibrary;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;

/**
 * High-level billing system logic, concerned with storing call events and creating customer bills.
 */
public class BillingSystem implements IBillingSystem {

	private Logger log = FileLogger.create();
	
    private ICallEventManager callEventManager;
    private ICallCostCalculator callCostCalculator;
    private IBillGenerator billGenerator;
    private IClock clock;
    private CustomerDatabase customerDatabase;
    
    /**
     * Constructor. To ensure rest of system continues to work without changes.
     */
    public BillingSystem() {
    	TariffLibrary tariffDatabase = CentralTariffDatabase.getInstance();
    	this.callEventManager = new CallEventManager();
		this.callCostCalculator = new CallCostCalculator(tariffDatabase, new DaytimePeakPeriod());
		this.billGenerator = new HtmlBillGenerator(new HtmlBillPrinter());
		this.customerDatabase = CentralCustomerDatabase.getInstance();
		this.clock = new Clock();
    }
    
    /**
     * Constructor. To be used for dependency injection.
     * @param callEventManager The call event manager to use when handling call events.
     * @param callCostCalculator The call cost calculator to use when generating bills.
     * @param billGenerator The bill generator to use to generate bills.
     * @param customerDatabase The customer database to refer to for customer information.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public BillingSystem(
    		ICallEventManager callEventManager,
    		ICallCostCalculator callCostCalculator,
    		IBillGenerator billGenerator,
    		CustomerDatabase customerDatabase,
    		IClock clock) {
    	AssertionHelper.NotNull(callEventManager, "callEventManager");
    	AssertionHelper.NotNull(callCostCalculator, "callCostCalculator");
    	AssertionHelper.NotNull(billGenerator, "billGenerator");
    	AssertionHelper.NotNull(customerDatabase, "customerDatabase");
    	AssertionHelper.NotNull(clock, "clock");
    	this.callEventManager = callEventManager;
    	this.callCostCalculator = callCostCalculator;
    	this.billGenerator = billGenerator;
    	this.customerDatabase = customerDatabase;
    	this.clock = clock;
    }

    /**
     * Called when a call is started.
     * @param caller The caller phone number.
     * @param callee The callee phone number.
     * @exception IllegalArgumentException If any of arguments are null.
     * @exception IllegalStateException Thrown if caller starts two calls with same callee simultaneously.
     */
    public void callInitiated(String caller, String callee) {
    	log.info("Call from " + caller + " to " + callee + " initiated.");
    	callEventManager.handleEvent(new CallStart(caller, callee, clock.now()));
    }

    /**
     * Called when a call is ended.
     * @param caller The caller phone number.
     * @param callee The callee phone number.
     * @exception IllegalArgumentException If any of arguments are null.
     */
    public void callCompleted(String caller, String callee) {
    	log.info("Call from " + caller + " to " + callee + " initiated.");
    	callEventManager.handleEvent(new CallEnd(caller, callee, clock.now()));
    }

    /**
     * Creates bills for all customers, prints them out and returns them as a list of type Bill.
     * @return ArrayList<Bill> The list of created bills, one per customer.
     */
    public ArrayList<Bill> createCustomerBills() {
        List<Customer> customers = customerDatabase.getCustomers();
        ArrayList<Bill> customerBills = new ArrayList<Bill>();
        log.info("About to create " + customers.size() + " customer bills.");
        
        for (Customer customer : customers) {
            Bill bill = createBillFor(customer);
            customerBills.add(bill);
        }
        
        callEventManager.clearCallLogs();
        log.info("All customer bills created and call logs cleared.");
        return customerBills;
    }

    /**
     * Creates a bill for a specific customer.
     * @param customer The customer to create a bill for.
     * @return Bill The customer's bill.
     */
    private Bill createBillFor(Customer customer) {
    	BigDecimal totalBill = new BigDecimal(0);
        List<LineItem> items = new ArrayList<LineItem>();
        List<Call> calls = callEventManager.getCallsForCustomer(customer.getPhoneNumber());
        
        if (calls == null) {
        	return new Bill(customer, items, MoneyFormatter.penceToPounds(totalBill));
        }

        for (Call call : calls) {
        	BigDecimal callCost = callCostCalculator.calculateCallCost(customer, call);
            totalBill = totalBill.add(callCost);
            items.add(new LineItem(call, callCost));
        }

        Bill bill = new Bill(customer, items, MoneyFormatter.penceToPounds(totalBill));
        billGenerator.generateBill(bill);
        return bill;
    }
}