package com.acmetelecom;

import java.util.List;

import com.acmetelecom.customer.Customer;

public class Bill {

	private Customer customer;
	private List<LineItem> calls;
	private String totalBill;
	
	public Bill(Customer customer, List<LineItem> calls, String totalBill) {
		this.customer = customer;
		this.calls = calls;
		this.totalBill = totalBill;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public List<LineItem> GetCalls() {
		return calls;
	}
	
	public String GetTotalBill() {
		return totalBill;
	}
}
