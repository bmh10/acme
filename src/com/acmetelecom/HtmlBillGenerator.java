package com.acmetelecom;

import com.acmetelecom.customer.Customer;

import java.util.List;

public class HtmlBillGenerator implements IBillGenerator {

	HtmlPrinter printer;
	
	public HtmlBillGenerator(HtmlPrinter printer) {
		this.printer = printer;
	}
	
    public void generateBill(Bill bill) {
    	Customer customer = bill.getCustomer();
        printer.printHeading(customer.getFullName(), customer.getPhoneNumber(), customer.getPricePlan());
        
        for (LineItem call : bill.GetCalls()) {
            printer.printItem(call.date(), call.callee(), call.durationMinutes(), MoneyFormatter.penceToPounds(call.cost()));
        }
        
        printer.printTotal(bill.GetTotalBill());
    }

}
