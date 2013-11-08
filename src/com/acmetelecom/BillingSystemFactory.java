package com.acmetelecom;

import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Factory for creating BillingSystem objects.
 */
public class BillingSystemFactory {

	/**
	 * Creates a standard billing system by first creating all necessary object to inject.
	 * @return The created BillingSystem.
	 */
	public BillingSystem create() {
		// Dependency injection.
		TariffLibrary tariffDatabase = CentralTariffDatabase.getInstance();
		CustomerDatabase customerDatabase = CentralCustomerDatabase.getInstance();
		CallCostCalculator callCostCalculator = new CallCostCalculator(tariffDatabase, new DaytimePeakPeriod());
		HtmlBillGenerator billGenerator = new HtmlBillGenerator(new HtmlBillPrinter());
		return new BillingSystem(callCostCalculator, billGenerator, customerDatabase);
	}
}
