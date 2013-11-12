package com.acmetelecom;

import java.util.List;

import com.acmetelecom.customer.Customer;

/**
 * Responsible for generating bills in HTML form.
 */
public class HtmlBillGenerator implements IBillGenerator {

	private IBillPrinter printer;
	
	/**
	 * Constructor.
	 * @param printer The printer to use when generating the bill.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	public HtmlBillGenerator(IBillPrinter printer) {
		AssertionHelper.NotNull(printer, "printer");
		this.printer = printer;
	}
	
	/**
	 * Generates the specified bill in HTML form and prints it out.
	 * @param customer The customer to generate the bill for.
	 * @param items The list of items to put in the bill.
	 * @param totalBill The total bill charge.
	 * @return The generated bill.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
    public Bill sendBill(Customer customer, List<LineItem> items, String totalBill) {
    	AssertionHelper.NotNull(customer, "customer");
    	AssertionHelper.NotNull(items, "items");
    	AssertionHelper.NotNull(totalBill, "totalBill");
    	
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        
        for (LineItem call : items) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        
        printer.printTotal(totalBill);
        return new Bill(customer, items, totalBill);
    }
}
