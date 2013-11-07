package com.acmetelecom;

import com.acmetelecom.customer.Customer;

/**
 * Responsible for generating bills in HTML form.
 */
public class HtmlBillGenerator implements IBillGenerator {

	HtmlBillPrinter printer;
	
	/**
	 * Constructor.
	 * @param printer The HTML printer to use when generating the bill.
	 */
	public HtmlBillGenerator(HtmlBillPrinter printer) {
		this.printer = printer;
	}
	
	/**
	 * Generates the specified bill in HTML form and prints it out.
	 * @param bill The Bill to generate.
	 */
    public void generateBill(Bill bill) {
    	Customer customer = bill.getCustomer();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        
        for (LineItem call : bill.GetCalls()) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        
        printer.printTotal(bill.GetTotalBill());
    }

}
