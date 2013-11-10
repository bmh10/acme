package com.acmetelecom;

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
	 * @param bill The Bill to generate.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
    public void generateBill(Bill bill) {
    	AssertionHelper.NotNull(bill, "bill");
    	Customer customer = bill.getCustomer();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        
        for (LineItem call : bill.GetCalls()) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        
        printer.printTotal(bill.GetTotalBill());
    }
}
