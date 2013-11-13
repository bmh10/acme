package com.acmetelecom;

import java.util.List;

import com.acmetelecom.customer.Customer;

/**
 * Stores information about a customer's bill.
 */
public class Bill {

	private Customer customer;
	private List<LineItem> items;
	private String totalBill;
	
	/**
	 * Constructor.
	 * @param customer The customer this bill is for.
	 * @param items A list of LineItems which each identifies a call the customer has made and its cost.
	 * @param totalBill The customer's total bill.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	public Bill(Customer customer, List<LineItem> items, String totalBill) {
		AssertionHelper.NotNull(customer, "customer");
		AssertionHelper.NotNull(items, "calls");
		AssertionHelper.NotNull(totalBill, "totalBill");
		this.customer = customer;
		this.items = items;
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
	 * Gets the list of items/calls this customer will be charged for.
	 * @return The list of items included in this bill.
	 */
	public List<LineItem> getItems() {
		return items;
	}
	
	/**
	 * Gets the customer's total bill.
	 * @return The customer's total bill as a String.
	 */
	public String getTotalBill() {
		return totalBill;
	}
}