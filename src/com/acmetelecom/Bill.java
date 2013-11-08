package com.acmetelecom;

import java.util.List;

import com.acmetelecom.customer.Customer;

/**
 * Stores information about a customer's bill.
 */
public class Bill {

	private Customer customer;
	private List<LineItem> calls;
	private String totalBill;
	
	/**
	 * Constructor.
	 * @param customer The customer this bill is for.
	 * @param calls A list of calls this customer has made.
	 * @param totalBill The customer's total bill.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	public Bill(Customer customer, List<LineItem> calls, String totalBill) {
		AssertionHelper.NotNull(customer, "customer");
		AssertionHelper.NotNull(calls, "calls");
		AssertionHelper.NotNull(totalBill, "totalBill");
		this.customer = customer;
		this.calls = calls;
		this.totalBill = totalBill;
	}
	
	/**
	 * Gets the customer this bill is for.
	 * @return The customer this bill is for.
	 */
	public Customer getCustomer() {
		return customer;
	}
	
	/**
	 * Gets the list of calls this customer will be charged for.
	 * @return The list of calls included in this bill.
	 */
	public List<LineItem> GetCalls() {
		return calls;
	}
	
	/**
	 * Gets the customer's total bill.
	 * @return The customer's total bill as a String.
	 */
	public String GetTotalBill() {
		return totalBill;
	}
}