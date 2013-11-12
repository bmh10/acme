package com.acmetelecom;

import java.util.List;

import com.acmetelecom.customer.Customer;

/**
 * The BillGenerator interface which all bill generators should implement.
 */
public interface IBillGenerator {
	
	/**
	 * Generates the specified bill and prints it out.
	 * @param customer The customer to generate the bill for.
	 * @param items The list of items to put in the bill.
	 * @param totalBill The total bill charge.
	 * @return The generated bill.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	Bill sendBill(Customer customer, List<LineItem> items, String totalBill);
}
